package ru.sumarokov.google_table.models.furmulas;

import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.IllegalCommandException;
import java.util.*;

@Component
public class Calculator {

    /**
     * Вычисляет значение используя необходимую формулу определенную в объекте класса "Formula" и
     * аргументы из этого же объекта
     *
     * @param formula объект класса Formula
     * @return число с плавающей точкой в строковом ввиде
     * @throws IllegalCommandException в случае если во время вычисления возникло исключительная ситуация,
     *                                 например деление на ноль
     */
    public String calculate(Formula formula) throws IllegalCommandException {
        switch (formula.getType()) {
            case Number:
                return calculateNumber(formula.getArgs().get(0));
            case Expression:
                return calculateExpression(formula.getArgs());
            case Sum:
                return calculateSum(formula.getArgs());
            default:
                throw new IllegalCommandException("No such formula");
        }
    }

    /**
     * Переводит из строкового значения в строковое в виде числа с плавающей точкой
     *
     * @param number число в строковом ввиде
     * @return число с плавающей точкой в строковом ввиде
     */
    private String calculateNumber(String number) {
        return String.valueOf(Double.parseDouble(number));
    }

    /**
     * Суммирует список чисел
     *
     * @param args список чисел в строковом ввиде
     * @return число с плавающей точкой в строковом ввиде
     */
    private String calculateSum(List<String> args) {
        double result = 0;
        for (String arg : args)
            result += Double.parseDouble(arg);
        return String.valueOf(result);
    }

    /**
     * Калькулятор математических выражений.
     *
     * @param tokens список операторов и чисел
     * @return результат математического выражения
     * @throws IllegalCommandException в случае если во время вычисления возникло исключительная ситуация,
     *                                 например деление на ноль
     */
    private String calculateExpression(List<String> tokens) throws IllegalCommandException {
        Deque<Double> numbers = new ArrayDeque<>();
        Deque<String> operations = new ArrayDeque<>();

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            while (!operations.isEmpty()
                    && !operations.getLast().equals("(")
                    && !token.equals("(")
                    && !isNumeric(token)
                    && compareOperation(operations.peek(), token) >= 0) {
                performAnOperation(numbers, operations);
            }

            if (token.equals(")")) {
                while (!operations.getLast().equals("(")) {
                    performAnOperation(numbers, operations);
                }
                operations.removeLast();
                continue;
            }

            if (isNumeric(token)) numbers.addLast(Double.parseDouble(token));
            else operations.addLast(token);
        }

        for (int i = 0; i < operations.size(); i++) {
            performAnOperation(numbers, operations);
        }
        return numbers.pop().toString();
    }

    /**
     * Достает из списка numbers последние 2 числа
     * и производит над ними математическую операцию,
     * взятую из списка operations,
     * после чего результат операции ложит в список numbers
     * @param numbers список чисел
     * @param operations список операций
     * @throws IllegalCommandException в случае если в списке operations
     * последний элемент окажется некорректным оператором
     */
    private void performAnOperation(Deque<Double> numbers, Deque<String> operations) throws IllegalCommandException {
        Double secondNumber = numbers.getLast();
        numbers.removeLast();
        Double firstNumber = numbers.getLast();
        numbers.removeLast();
        String operator = operations.getLast();
        operations.removeLast();
        Double result = performAnOperation(firstNumber, secondNumber, operator);
        numbers.addLast(result);
    }

    /**
     * Производит математическу операцию над 2-я числами
     *
     * @param firstNumber первое число
     * @param secondNumber второе число
     * @param operator математический оператор
     * @return результат операции
     */
    private Double performAnOperation(Double firstNumber, Double secondNumber, String operator) throws IllegalCommandException {
        Double result = 0.0;
        switch (operator) {
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
                    throw new IllegalCommandException("Incorrect value. During the calculation, a division by zero has occurred");
                result = firstNumber / secondNumber;
                break;
        }
        return result;
    }

    /**
     * Сравнивает приоритеты операторов.
     *
     * @return 0 если равны,
     * -1 если firstOperation меньше чем secondOperation,
     * 1 если firstOperation больше чем secondOperation
     */
    private int compareOperation(String firstOperation, String secondOperation) {
        return getPriorityOfOperations(firstOperation) -
                getPriorityOfOperations(secondOperation);
    }

    /**
     * @return возвращает приоритет операции в виде целочисленного числа.
     * Чем больше приоритет тем больше число.
     * Если полученный символ не является валидным оператором вернется число -1
     */
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
}
