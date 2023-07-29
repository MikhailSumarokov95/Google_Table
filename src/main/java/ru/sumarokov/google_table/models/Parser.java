package ru.sumarokov.google_table.models;

import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.furmulas.Formula;
import ru.sumarokov.google_table.models.furmulas.FormulaPrefixes;
import ru.sumarokov.google_table.models.furmulas.FormulaType;

import java.util.*;
import java.util.regex.*;

@Component
public class Parser {

    // Validator

    public Formula parse(Table table, Cell cell) {
        String value = cell.getValue().toUpperCase();

        if (value.startsWith(FormulaPrefixes.EXPRESSION))
            return parseExpression(table, cell);

        else if (value.startsWith(FormulaPrefixes.SUM))
            return parseSum(table, value);

        else return parseNumber(value);
    }

    private Formula parseNumber(String value) {
        List<String> args = new ArrayList<>();
        args.add(value);
        return new Formula(FormulaType.Number, args);
     }

    private Formula parseExpression(Table table, Cell cell) {
        StringBuilder expression = new StringBuilder();
        String value = cell.getValue();

        for (int i = 1; i < value.length(); i++) {
            if (Character.isLetter(value.charAt(i))) {
                String id = String.valueOf(value.charAt(i)) + value.charAt(i + 1);
                String valueCell = table.getValueCell(id);
                expression.append(valueCell);
            }
            else if (i != 1 && Character.isLetter(value.charAt(i - 1))) continue;
            else expression.append(value.charAt(i));
        }
        List<String> args = new ArrayList<>();
        args.add(expression.toString());

        return new Formula(FormulaType.Expression, args);
    }

    private Formula parseSum(Table table, String value) {
        Pattern pattern = Pattern.compile("[A-Z][0-9]:[A-Z][0-9]");
        Matcher matcher = pattern.matcher(value);
        matcher.find();
        String[] args = matcher.group().split(":");
        args = Arrays.stream(args)
                .sorted()
                .toArray(String[]::new);

        List<String> argsForSum = getListOfArgs(args[0], args[1]);
        for (int i = 0; i < argsForSum.size(); i++)
            argsForSum.set(i, table.getValueCell(argsForSum.get(i)));
        return new Formula(FormulaType.Sum, argsForSum);
    }

    private List<String> getListOfArgs(String argFirst, String argSecond) throws IllegalArgumentException {
        ArrayList<String> args = new ArrayList<>();

        if (argFirst.compareTo(argSecond) >= 0)
            throw new IllegalArgumentException("The first argument cannot be greater than the second");

        if (argFirst.charAt(0) == argSecond.charAt(0)) {
            int firstLine = Integer.parseInt(argFirst.substring(1));
            int secondLine = Integer.parseInt(argSecond.substring(1));
            char column = argFirst.charAt(0);
            for (int i = firstLine; i < secondLine + 1; i++) {
                String arg = String.valueOf(column) + i;
                args.add(arg);
            }
        }
        else {
            char firstLine = argFirst.charAt(0);
            char secondLine = argSecond.charAt(0);
            char line = argFirst.charAt(1);
            for (char i = firstLine; i < secondLine + 1; i++) {
                String arg = String.valueOf(i) + line;
                args.add(arg);
            }
        }
        return args;
    }
}
