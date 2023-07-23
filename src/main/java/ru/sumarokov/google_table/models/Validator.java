package ru.sumarokov.google_table.models;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Validator {

    public void validate(String value) throws Exception {
        if (value.equals("")) throw new Exception("Enter the correct number");
        else if (value.charAt(0) == '=') validateFormula(value);
        else validateNumber(value);
    }

    private void validateFormula(String expression) throws Exception {
        char[] expressionChar = expression.toCharArray();

        validateEquals(expressionChar);
        validateIncorrectOperator(expression);
        validateOperatorIsEndChar(expressionChar);
        validateDefectiveBracket(expressionChar);
        validateBracketIsEmpty(expression);
        validateCorrectSequenceOperatorsAndNumbers(expression);
        validateDivideByZero(expression);
        validateSubtraction(expression);
        validateCorrectLocationDot(expression);
    }

    private void validateEquals(char[] expression) throws Exception {
        for (int i = 1; i < expression.length; i++)
            if (expression[i] == '=')
                throw new Exception("Incorrect value. 2 equals");
    }

    private void validateIncorrectOperator(String expression) throws Exception {
        Pattern pattern = Pattern.compile("[^а-яА-ЯёЁa-zA-Z0-9*/+.=)(-]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new Exception("Incorrect value. Invalid operator symbol");
    }

    private void validateOperatorIsEndChar(char[] expression) throws Exception {
        char endChar = expression[expression.length - 1];
        if (endChar == '*' || endChar == '/'
                || endChar == '+' || endChar == '.'
                || endChar == '=' || endChar == '('
                || endChar == '-')
            throw new Exception("Incorrect value. Operator cannot be at the end of an expression");
    }

    private void validateDefectiveBracket(char[] expression) throws Exception {
        int bracketNumberDifference = 0;
        for (int i = 1; i < expression.length; i++) {
            if (expression[i] == '(') bracketNumberDifference++;
            else if (expression[i] == ')') bracketNumberDifference--;
        }
        if (bracketNumberDifference != 0)
            throw new Exception("Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    private void validateBracketIsEmpty(String expression) throws Exception {
        Pattern pattern = Pattern.compile("\\(\\)");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new Exception("Brackets cannot be empty");
    }

    private void validateCorrectSequenceOperatorsAndNumbers(String expression) throws Exception {
        Pattern pattern = Pattern.compile("[*/+][*/+]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new Exception("Incorrect value. The operator must be followed by a number or a reference");
    }

    private void validateDivideByZero(String expression) throws Exception {
        Pattern pattern = Pattern.compile("\\/0");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new Exception("Incorrect value. Can't divide by zero");
    }

    private void validateSubtraction(String expression) throws Exception {
        Pattern pattern = Pattern.compile("-[^\\w()]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new Exception("Incorrect value. Incorrect location of the '-' sign");
    }

    private void validateCorrectLocationDot(String expression) throws Exception {
        Pattern pattern = Pattern.compile("[^0-9]\\.|\\.[^0-9]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new Exception("Incorrect value. Incorrect location of the '.' sign");
    }

    private void validateNumber(String value) throws Exception {
        try {
            Double.parseDouble(value);
        } catch (Exception ex) {
            throw new Exception("Enter the correct number");
        }
    }
}
