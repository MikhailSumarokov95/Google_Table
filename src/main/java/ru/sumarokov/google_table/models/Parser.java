package ru.sumarokov.google_table.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.dao.TableDAO;
import ru.sumarokov.google_table.models.furmulas.*;
import java.util.*;
import java.util.regex.*;

@Component
public class Parser {

    Validator validator;

    @Autowired
    public Parser(Validator validator) {
        this.validator = validator;
    }

    /**
     * Парсит значение ячейки в объект класса "Formula"
     * определяющий формулу и её аргументы необходимые для вычисления.
     * Предварительно отправляет значение ячейки на валидацию
     *
     * @param tableDAO БД с данными таблицы,
     * @param cell     ячейка с данными для парсинга
     * @return объект класса Formula
     * @throws IllegalCommandException в случае если cell содержит некорректное значение (поле value)
     */
    public Formula parse(TableDAO tableDAO, Cell cell) throws IllegalCommandException {
        validator.validate(tableDAO, cell);

        String value = cell.getValue().toUpperCase();

        if (value.startsWith(FormulaPrefixes.EXPRESSION))
            return parseExpression(tableDAO, cell);

        else if (value.startsWith(FormulaPrefixes.SUM))
            return parseSum(tableDAO, value);

        else return parseNumber(value);
    }

    /**
     * @param value значение для парсинга
     * @return объект класса Formula с полем type = FormulaType.Number
     */
    private Formula parseNumber(String value) {
        List<String> args = new ArrayList<>();
        args.add(value);
        return new Formula(FormulaType.Number, args);
    }

    /**
     * Парсит математическое выражение помещая в список аргументов числа и операции над ними
     * предварительно заменив все ссылки на ячейки из таблицы
     *
     * @param tableDAO БД с данными таблицы,
     * @param cell     ячейка с данными для парсинга
     * @return объект класса Formula с полем type = FormulaType.Expression
     * и поле args со списком чисел и операций
     */
    private Formula parseExpression(TableDAO tableDAO, Cell cell) {
        String value = replacingReferencesWithValue(tableDAO, cell.getValue());
        List<String> args = parseToListToken(value);
        return new Formula(FormulaType.Expression, args);
    }

    /**
     * Заменяет ссылки на числа
     *
     * @param tableDAO БД с данными таблицы,
     * @param value строка ссылками для замены их на значения из ячеек таблицы из БД(tableDAO)
     * @return строку с замененными ссылками на значения
     */
    private String replacingReferencesWithValue(TableDAO tableDAO, String value) {
        StringBuilder expression = new StringBuilder();
        for (int i = 1; i < value.length(); i++) {
            if (Character.isLetter(value.charAt(i))) {
                String id = String.valueOf(value.charAt(i)) + value.charAt(i + 1);
                String valueCell = tableDAO.getValueCell(id);
                expression.append(valueCell);
            } else if (i != 1 && Character.isLetter(value.charAt(i - 1))) continue;
            else expression.append(value.charAt(i));
        }
        return expression.toString();
    }

    /**
     * Парсит формулу "Sum".
     * В аргументы заносит список чисел для суммирования
     *
     * @param tableDAO БД с данными таблицы,
     * @param value    данные для парсинга
     * @return объект класса Formula с полем type = FormulaType.Sum
     * и поле args со списком чисел
     */
    private Formula parseSum(TableDAO tableDAO, String value) {
        Pattern pattern = Pattern.compile("[A-Za-z][0-9]:[A-Za-z][0-9]");
        Matcher matcher = pattern.matcher(value);
        matcher.find();
        String[] args = matcher.group().toUpperCase().split(":");
        args = Arrays.stream(args)
                .sorted()
                .toArray(String[]::new);

        List<String> argsForSum = getListOfArgs(args[0], args[1]);
        for (int i = 0; i < argsForSum.size(); i++)
            argsForSum.set(i, tableDAO.getValueCell(argsForSum.get(i)));
        return new Formula(FormulaType.Sum, argsForSum);
    }

    /**
     * Создает список ссылок на ячейки расположенных в диапазоне между argFirst и argSecond
     * включая крайние значения
     *
     * @param argFirst  первая ссылка,
     * @param argSecond последняя ссылка
     * @return список ссылок на ячейки расположенных
     * @throws IllegalArgumentException если первая ссылка оказалась больше последней
     */
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
        } else {
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

    /**
     * Преобразует математическое выражение в список операторов и числовых значений
     *
     * @param expression математическое выражение для разделения на токены
     * @return список операторов и числовых значений математического выражения
     */
    private List<String> parseToListToken(String expression) {
        List<String> listTokens = new ArrayList<>(expression.length());

        String token = new String();
        for (int i = 0; i < expression.length(); i++) {
            token += expression.charAt(i);
            if (i != (expression.length() - 1)
                    && ((Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.'))
                    && ((Character.isDigit(expression.charAt(i + 1))) || expression.charAt(i + 1) == '.')) continue;
            else if ((expression.charAt(i) == '-' && Character.isDigit(expression.charAt(i + 1)))
                    && (i == 0 || !Character.isDigit(expression.charAt(i - 1)))) continue;
            else {
                listTokens.add(token);
                token = "";
            }
        }
        return listTokens;
    }
}
