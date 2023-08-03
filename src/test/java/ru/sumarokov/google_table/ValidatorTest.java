package ru.sumarokov.google_table;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.sumarokov.google_table.models.*;
import ru.sumarokov.google_table.models.dao.TableDAO;

public class ValidatorTest {

    private final Validator validator = new Validator();
    private final static String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private final static String URL = "jdbc:postgresql://localhost:5432/test";
    private final static String USERNAME = "postgres";
    private final static String PASSWORD = "123";

    @Test
    public void validateCorrectExpression() {
        Cell cell = new Cell("A", "1",
                "=1+2*(3+4/2-(1+-2))*2+1+(20.3+13)*(1+21)");
        try {
            validator.validate(getCorrectTable(), cell);
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void validateCorrectFormulaSUMFirst() {
        Cell cell = new Cell("A", "1","SUM(B1:B3)");
        try {
            validator.validate(getCorrectTable(), cell);
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void validateCorrectFormulaSUMSecond() {
        Cell cell = new Cell("A", "1","SUM(A2:C2)");
        try {
            validator.validate(getCorrectTable(), cell);
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void validateCorrectNumber() {
        Cell cell = new Cell("A", "1", "512");
        try {
            validator.validate(getCorrectTable(), cell);
        } catch (IllegalCommandException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void validateSUMReferenceCellToDownCase() {
        Cell cell = new Cell("A", "1", "SUM(b1:b3)");
        try {
            validator.validate(getCorrectTable(), cell);
        } catch (IllegalCommandException ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test()
    public void validateTwoEquals() {
        Cell cell = new Cell("A", "1", "=1=2*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. 2 equals");
    }

    @Test()
    public void validateIncorrectOperatorExclamationPoint() {
        Cell cell = new Cell("A", "1", "=1!2*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Invalid operator symbol");
    }

    @Test()
    public void validateIncorrectOperatorUnderscore() {
        Cell cell = new Cell("A", "1", "=1_2*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Invalid operator symbol");
    }

    @Test()
    public void validateIncorrectOperatorAt() {
        Cell cell = new Cell("A", "1", "=1@2*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Invalid operator symbol");
    }

    @Test()
    public void validateOperatorPlusIsEndChar() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2+1+");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorMinusIsEndChar() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2+1-");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorMultiplyIsEndChar() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2+1*");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorDivideIsEndChar() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2+1/");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorDotIsEndChar() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2+1.");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateOperatorBracketOpensIsEndChar() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2+1(");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Operator cannot be at the end of an expression");
    }

    @Test()
    public void validateDefectiveBracket() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2)+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    @Test()
    public void validateExtraClosingBracket() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2)+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    @Test()
    public void validateExtraOpeningBracket() {
        Cell cell = new Cell("A", "1", "=(1+2*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    @Test()
    public void validateEmptyBrackets() {
        Cell cell = new Cell("A", "1", "=1+2*(()3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Brackets cannot be empty");
    }

    @Test()
    public void validateTwoPlusesNextToEachOther() {
        Cell cell = new Cell("A", "1", "=1+2*(3++4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateTwoMultiplyNextToEachOther() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))**2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateTwoDivideNextToEachOther() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4//2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateDivideBeforeMultiply() {
        Cell cell = new Cell("A", "1", "=1+2/*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validatePlusBeforeMultiply() {
        Cell cell = new Cell("A", "1", "=1+2+*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateMultiplyBeforePlus() {
        Cell cell = new Cell("A", "1", "=1+2*+(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. The operator must be followed by a number or a reference");
    }

    @Test()
    public void validateDivideByZero() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/0-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Can't divide by zero");
    }

    @Test()
    public void validateMinusBeforeMultiply() {
        Cell cell = new Cell("A", "1", "=1+2-*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Incorrect location of the '-' sign");
    }

    @Test()
    public void validateMinusBeforePlus() {
        Cell cell = new Cell("A", "1", "=1-+2*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Incorrect location of the '-' sign");
    }

    @Test()
    public void validateDotBeforeMultiply() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2)).*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Incorrect location of the '.' sign");
    }

    @Test()
    public void validateDotBeforeNumber() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*.2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Incorrect location of the '.' sign");
    }

    @Test()
    public void validateDotAfterNumber() {
        Cell cell = new Cell("A", "1", "=1+2*(3+4/2-(1+2))*2.+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Incorrect location of the '.' sign");
    }

    @Test
    public void validateNumberWithMultiply() {
        Cell cell = new Cell("A", "1", "512*");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Enter the correct number");
    }

    @Test
    public void validateExpressionWithoutEquals() {
        Cell cell = new Cell("A", "1", "1+2*(3+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Enter the correct number");
    }

    @Test
    public void validateCellIsNull() {
        waitExceptionFromValidator(getCorrectTable(), null,
                "Enter the value");
    }

    @Test
    public void validateValueIsNull() {
        Cell cell = new Cell("A", "1", null);
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Enter the value");
    }

    @Test
    public void validateValueIsEmpty() {
        Cell cell = new Cell("A", "1", "");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Enter the value");
    }

    @Test
    public void validateReferenceToCellThatDoesNotExit() {
        Cell cell = new Cell("A", "1", "1+2*(F9+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. There is a reference to a non-existent cell or you refer to an empty cell");
    }

    @Test
    public void validateReferenceToCellThatEmpty() {
        Cell cell = new Cell("A", "1", "1+2*(B1+4/2-(1+2))*2+1");
        TableDAO tableDAO = getCorrectTable();
        tableDAO.setValueCell("B1", "");
        waitExceptionFromValidator(tableDAO, cell,
                "Incorrect value. There is a reference to a non-existent cell or you refer to an empty cell");
    }

    @Test
    public void validateCellReferenceToItself() {
        Cell cell = new Cell("A", "1", "1+2*(A1+4/2-(1+2))*2+1");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Cell must not refer to itself");
    }

    @Test
    public void validateSUMWithOneArg() {
        Cell cell = new Cell("A", "1", "SUM(:B3)");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value.Enter the correct value of the SUM formula");
    }

    @Test
    public void validateSUMDiagonalRange() {
        Cell cell = new Cell("A", "1", "SUM(A2:B3)");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Cells must be in the same line or column");
    }

    @Test
    public void validateSUMReferenceCellToItselfInCellRange() {
        Cell cell = new Cell("B", "2", "SUM(B1:B3)");
        waitExceptionFromValidator(getCorrectTable(), cell,
                "Incorrect value. Cell cannot be inside a range");
    }

    private void waitExceptionFromValidator(TableDAO table, Cell cell, String expectedMessage) {
        Exception thrown = Assertions.assertThrows(Exception.class, () ->
                validator.validate(table, cell));
        Assertions.assertEquals(expectedMessage, thrown.getMessage());
    }

    private TableDAO getCorrectTable() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        TableDAO table = new TableDAO(new JdbcTemplate(dataSource));
        table.setValueCell("A1", "1");
        table.setValueCell("A2", "0,12333333333333333333");
        table.setValueCell("A3", "321");
        table.setValueCell("B1", "21333333333333333333");
        table.setValueCell("B2", "21333333333333333333");
        table.setValueCell("B3", "0");
        table.setValueCell("C1", "-1");
        table.setValueCell("C2", "-1233333333333333333333333");
        table.setValueCell("C3", "-0,12333");
        return table;
    }
}
