package ru.sumarokov.google_table.models;

import org.springframework.stereotype.Component;

@Component
public class Validator {

    public void validate(String value) throws Exception {
        if (value.equals("")) throw new Exception("Enter the correct number");
        else if (value.charAt(0) == '=') validateFormula(value);
        else validateNumber(value);
    }

    private void validateFormula(String value) throws Exception {
        char[] valueChar = value.toCharArray();

        int bracketNumberDifference = 0;
        for (int i = 0; i < valueChar.length; i++) {

            if (i != 0 && valueChar[i] == '=')
                throw new Exception("Incorrect value. 2 equals");

            if (!Character.isLetterOrDigit(valueChar[i]) && valueChar[i] != '/' && valueChar[i] != '*'
                    && valueChar[i] != '+' && valueChar[i] != '-' && valueChar[i] != '.'
                    && valueChar[i] != ')' && valueChar[i] != '(' && valueChar[i] != '=')
                throw new Exception("Incorrect value. Invalid operator symbol");

            if ((!Character.isLetterOrDigit(valueChar[i]) && !(valueChar[i] == '-'))
                    && (i == valueChar.length - 1
                    || (!Character.isLetterOrDigit(valueChar[i + 1])
                    && valueChar[i] != ')' && valueChar[i] != '('
                    && valueChar[i + 1] != ')' && valueChar[i + 1] != '('
                    && valueChar[i + 1] != '-')))
                throw new Exception("Incorrect value. The sign must be followed by a number or a reference");

            if (valueChar[i] == '/' && valueChar[i + 1] == '0')
                throw new Exception("Incorrect value. Can't divide by zero");

            if (valueChar[i] == '(') bracketNumberDifference++;
            else if (valueChar[i] == ')') bracketNumberDifference--;

            if (valueChar[i] == '-') {
                if (i == 0) {
                    if (!Character.isLetterOrDigit(valueChar[i + 1]))
                        throw new Exception("Incorrect value. Incorrect location of the '-' sign");
                } else {
                    if (i == valueChar.length - 1)
                        throw new Exception("Incorrect value. Sing '-' at the end of the expression");
                    if (!(Character.isLetterOrDigit(valueChar[i + 1]) || Character.isDigit(valueChar[i - 1])))
                        if (!((valueChar[i - 1]) == ')' && (valueChar[i + 1]) == '('))
                            throw new Exception("Incorrect value. Incorrect location of the '-' sign");
                }
            }
        }

        if (bracketNumberDifference != 0)
            throw new Exception("Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    private void validateNumber(String value) throws Exception {
        try {
            Double.parseDouble(value);
        } catch (Exception ex) {
            throw new Exception("Enter the correct number");
        }
    }
}
