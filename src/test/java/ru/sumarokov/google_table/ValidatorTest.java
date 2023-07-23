package ru.sumarokov.google_table;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sumarokov.google_table.models.Validator;

public class ValidatorTest {

    private Validator validator = new Validator();

    @Test
    public void validateCorrectExpression() {
        String expression = "=1+2*(3+4/2-(1+-2))*2+1+(20.3+13)*(1+21)";
        try {
            validator.validate(expression);
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test()
    public void validateTwoEquals() {
        waitExceptionFromValidator("=1=2*(3+4/2-(1+2))*2+1",
                "Incorrect value. 2 equals");
    }

    @Test()
    public void validateIncorrectOperatorExclamationPoint() {
        waitExceptionFromValidator("=1!2*(3+4/2-(1+2))*2+1",
                "Incorrect value. Invalid operator symbol");
    }

    @Test()
    public void validateIncorrectOperatorUnderscore() {
        waitExceptionFromValidator("=1_2*(3+4/2-(1+2))*2+1",
                "Incorrect value. Invalid operator symbol");
    }

    @Test()
    public void validateIncorrectOperatorAt() {
        waitExceptionFromValidator("=1@2*(3+4/2-(1+2))*2+1",
                "Incorrect value. Invalid operator symbol");
    }

    @Test()
    public void validateOperatorPlusIsEndChar() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2+1+",
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorMinusIsEndChar() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2+1-",
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorMultiplyIsEndChar() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2+1*",
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorDivideIsEndChar() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2+1/",
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorDotIsEndChar() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2+1.",
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorBracketOpensIsEndChar() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2+1(",
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateDefectiveBracket() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2)+1",
                "Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    @Test()
    public void validateExtraClosingBracket() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2)+1",
                "Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    @Test()
    public void validateExtraOpeningBracket() {
        waitExceptionFromValidator("=(1+2*(3+4/2-(1+2))*2+1",
                "Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    @Test()
    public void validateEmptyBrackets() {
        waitExceptionFromValidator("=1+2*(()3+4/2-(1+2))*2+1",
                "Brackets cannot be empty");
    }

    @Test()
    public void validateTwoPlusesNextToEachOther() {
        waitExceptionFromValidator("=1+2*(3++4/2-(1+2))*2+1",
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateTwoMultiplyNextToEachOther() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))**2+1",
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateTwoDivideNextToEachOther() {
        waitExceptionFromValidator("=1+2*(3+4//2-(1+2))*2+1",
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateDivideBeforeMultiply() {
        waitExceptionFromValidator("=1+2/*(3+4/2-(1+2))*2+1",
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validatePlusBeforeMultiply() {
        waitExceptionFromValidator("=1+2+*(3+4/2-(1+2))*2+1",
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateMultiplyBeforePlus() {
        waitExceptionFromValidator("=1+2*+(3+4/2-(1+2))*2+1",
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateDivideByZero() {
        waitExceptionFromValidator("=1+2*(3+4/0-(1+2))*2+1",
                "Incorrect value. Can't divide by zero");
    }

    @Test()
    public void validateMinusBeforeMultiply() {
        waitExceptionFromValidator("=1+2-*(3+4/2-(1+2))*2+1",
                "Incorrect value. Incorrect location of the '-' sign");
    }

    @Test()
    public void validateMinusBeforePlus() {
        waitExceptionFromValidator("=1-+2*(3+4/2-(1+2))*2+1",
                "Incorrect value. Incorrect location of the '-' sign");
    }

    @Test()
    public void validateDotBeforeMultiply() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2)).*2+1",
                "Incorrect value. Incorrect location of the '-' sign");
    }

    @Test()
    public void validateDotBeforeNumber() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*.2+1",
                "Incorrect value. Incorrect location of the '-' sign");
    }

    @Test()
    public void validateDotAfterNumber() {
        waitExceptionFromValidator("=1+2*(3+4/2-(1+2))*2.+1",
                "Incorrect value. Incorrect location of the '-' sign");
    }

    @Test
    public void validateCorrectNumber() {
        String value = "512";
        try {
            validator.validate(value);
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void validateNumberWithMultiply() {
        waitExceptionFromValidator("512*",
                "Enter the correct number");
    }

    @Test
    public void validateExpressionWithoutEquals() {
        waitExceptionFromValidator("1+2*(3+4/2-(1+2))*2+1",
                "Enter the correct number");
    }

    private void waitExceptionFromValidator(String expression, String expectedMessage) {
        Exception thrown = Assertions.assertThrows(Exception.class, () ->
                validator.validate(expression));
        Assertions.assertEquals(expectedMessage, thrown.getMessage());
    }
}
