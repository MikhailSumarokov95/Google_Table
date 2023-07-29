package ru.sumarokov.google_table.models.furmulas;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class Calculator {

    public String calculate(Formula formula) throws Exception {
        switch (formula.getType()) {
            case Number: return formula.args.get(0);
            case Expression: return calculateExpression(formula.args.get(0));
            case Sum: return calculateSum(formula.args);
            default: throw new Exception("No such formula");
        }
    }

    private String calculateSum(List<String> args) {
        double result = 0;
        for (String arg : args)
            result += Double.parseDouble(arg);
        return String.valueOf(result);
    }

    private String calculateExpression(String expression) throws Exception {
        Stack<Double> numbers = new Stack<>();
        Stack<String> operations = new Stack<>();
        List<String> tokens = parseToListToken(expression);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            while (!operations.empty()
                    && !operations.peek().equals("(")
                    && !token.equals("(")
                    && !isNumeric(token)
                    && compareOperation(operations.peek(), token) >= 0 ) {
                performAnOperation(numbers, operations);
            }

            if (token.equals(")")) {
                while (!operations.peek().equals("(")) {
                    performAnOperation(numbers, operations);
                }
                operations.pop();
                continue;
            }
            if (isNumeric(token)) numbers.push(Double.parseDouble(token));
            else operations.push(token);
        }
        while (!operations.empty()) {
            performAnOperation(numbers, operations);
        }
        return numbers.pop().toString();
    }

    private void performAnOperation(Stack<Double> numbers, Stack<String> operations) throws Exception {
        Double secondNumber = numbers.pop();
        Double firstNumber = numbers.pop();
        Double result = 0.0;
        switch (operations.pop()) {
            case "+":
                result = firstNumber + secondNumber;
                break;
            case "-":
                result = firstNumber - secondNumber;
                break;
            case "*":
                result = firstNumber * secondNumber;
                break;
            case "/":
                if (secondNumber.equals(0d))
                    throw new Exception("Incorrect value. During the calculation, a division by zero has occurred");
                result = firstNumber / secondNumber;
                break;
        }
        numbers.push(result);
    }

    private int compareOperation(String firstOperation, String secondOperation) {
        return getPriorityOfOperations(firstOperation) -
                getPriorityOfOperations(secondOperation);
    }

    private int getPriorityOfOperations(String operation) {
        switch (operation) {
            case "+":
            case "-":
                return 1;
            case "/":
            case "*":
                return 2;
        }
        return -1;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<String> parseToListToken(String expression) {
        List<String> listExpression = new ArrayList<>(expression.length());

        String token = new String();
        for (int i = 0; i < expression.length(); i++) {
            token += expression.charAt(i);
            if (i != (expression.length() - 1)
                    && ((Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.'))
                    && ((Character.isDigit(expression.charAt(i + 1))) || expression.charAt(i + 1) == '.')) continue;
            else if ((expression.charAt(i) == '-' && Character.isDigit(expression.charAt(i + 1)))
                     && (i == 0 || !Character.isDigit(expression.charAt(i - 1))) ) continue;
            else {
                listExpression.add(token);
                token = "";
            }
        }
        return listExpression;
    }
}
