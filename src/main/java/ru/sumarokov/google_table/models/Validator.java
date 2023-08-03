package ru.sumarokov.google_table.models;

import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.dao.TableDAO;
import ru.sumarokov.google_table.models.furmulas.FormulaPrefixes;
import java.util.regex.*;

@Component
public class Validator {

    /**
     * Валидация значения ячейки
     * @param tableDAO БД с данными таблицы
     * @param cell ячейка с данными для вычисления
     * @throws IllegalCommandException в случая некорректного значения
     * с описанием причины непрохождения валидации
     */

    public void validate(TableDAO tableDAO, Cell cell) throws IllegalCommandException {
        validateGeneral(tableDAO, cell);

        String value = cell.getValue().toUpperCase();

        if (value.startsWith(FormulaPrefixes.EXPRESSION))
            validateExpression(cell);

        else if (value.startsWith(FormulaPrefixes.SUM))
            validateSum(cell);

        else validateNumber(cell);
    }

    private void validateGeneral(TableDAO tableDAO, Cell cell) throws IllegalCommandException {
        validateEmptyCell(cell);
        validateReferenceToCellThatDoesNotExitsOrEmpty(tableDAO, cell);
        validateCellReferenceToItself(cell);
    }

    // Валидация выражения
    private void validateExpression(Cell cell) throws IllegalCommandException {
        String expression = cell.getValue();
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

    // Валидация числа
    private void validateNumber(Cell cell) throws IllegalCommandException {
        try {
            Double.parseDouble(cell.getValue());
        } catch (NumberFormatException ex) {
            throw new IllegalCommandException("Enter the correct number");
        }
    }

    // Валидация формулы "SUM"
    private void validateSum(Cell cell) throws IllegalCommandException {
        validateCurrentFormulaSUM(cell);
        validateDiagonalRange(cell);
        validateReferenceCellToItselfInCellRange(cell);
    }

    private void validateEmptyCell(Cell cell) throws IllegalCommandException {
        if (cell == null) throw new IllegalCommandException("Enter the value");
        String value = cell.getValue();
        if (value == null || value.isEmpty())
            throw new IllegalCommandException("Enter the value");
    }

    private void validateCellReferenceToItself(Cell cell) throws IllegalCommandException {
        Pattern pattern = Pattern.compile(cell.getId());
        Matcher matcher = pattern.matcher(cell.getValue());
        if (matcher.find())
            throw new IllegalCommandException("Incorrect value. Cell must not refer to itself");
    }

    private void validateReferenceToCellThatDoesNotExitsOrEmpty(TableDAO tableDAO, Cell cell) throws IllegalCommandException {
        String value = cell.getValue();
        for (int i = 1; i < value.length(); i++) {
            if (Character.isLetter(value.charAt(i)) && Character.isDigit(value.charAt(i + 1))) {
                String id = String.valueOf(value.charAt(i)) + value.charAt(i + 1);
                if (!tableDAO.hasCell(id) || tableDAO.getValueCell(id).isEmpty())
                    throw new IllegalCommandException("Incorrect value. There is a reference to a non-existent cell or you refer to an empty cell");
            }
        }
    }

    private void validateEquals(char[] expression) throws IllegalCommandException {
        for (int i = 1; i < expression.length; i++)
            if (expression[i] == '=')
                throw new IllegalCommandException("Incorrect value. 2 equals");
    }

    private void validateIncorrectOperator(String expression) throws IllegalCommandException {
        Pattern pattern = Pattern.compile("[^а-яА-ЯёЁa-zA-Z0-9*/+.=)(-]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new IllegalCommandException("Incorrect value. Invalid operator symbol");
    }

    private void validateOperatorIsEndChar(char[] expression) throws IllegalCommandException {
        char endChar = expression[expression.length - 1];
        if (endChar == '*' || endChar == '/'
                || endChar == '+' || endChar == '.'
                || endChar == '=' || endChar == '('
                || endChar == '-')
            throw new IllegalCommandException("Incorrect value. Operator cannot be at the end of an expression");
    }

    private void validateDefectiveBracket(char[] expression) throws IllegalCommandException {
        int bracketNumberDifference = 0;
        for (int i = 1; i < expression.length; i++) {
            if (expression[i] == '(') bracketNumberDifference++;
            else if (expression[i] == ')') bracketNumberDifference--;
        }
        if (bracketNumberDifference != 0)
            throw new IllegalCommandException("Incorrect value. The number of opening brackets is not equal to the number of closing brackets");
    }

    private void validateBracketIsEmpty(String expression) throws IllegalCommandException {
        Pattern pattern = Pattern.compile("\\(\\)");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new IllegalCommandException("Brackets cannot be empty");
    }

    private void validateCorrectSequenceOperatorsAndNumbers(String expression) throws IllegalCommandException {
        Pattern pattern = Pattern.compile("[*/+][*/+]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new IllegalCommandException("Incorrect value. The operator must be followed by a number or a reference");
    }

    private void validateDivideByZero(String expression) throws IllegalCommandException {
        Pattern pattern = Pattern.compile("/0");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new IllegalCommandException("Incorrect value. Can't divide by zero");
    }

    private void validateSubtraction(String expression) throws IllegalCommandException {
        Pattern pattern = Pattern.compile("-[^\\w()]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new IllegalCommandException("Incorrect value. Incorrect location of the '-' sign");
    }

    private void validateCorrectLocationDot(String expression) throws IllegalCommandException {
        Pattern pattern = Pattern.compile("[^0-9]\\.|\\.[^0-9]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find())
            throw new IllegalCommandException("Incorrect value. Incorrect location of the '.' sign");
    }

    private void validateCurrentFormulaSUM(Cell cell) throws IllegalCommandException {
        getRangeArguments(cell);
    }

    private void validateDiagonalRange(Cell cell) throws IllegalCommandException {
        String[] args = getRangeArguments(cell);
        if (args[0].charAt(0) != args[1].charAt(0)
                && args[0].charAt(1) != args[1].charAt(1))
            throw new IllegalCommandException("Incorrect value. Cells must be in the same line or column");
    }

    private void validateReferenceCellToItselfInCellRange(Cell cell) throws IllegalCommandException {
        String[] args = getRangeArguments(cell);
        String idFirstCell = args[0];
        String idSecondCell = args[1];
        String idTargetCell = cell.getId();

        if (!((idTargetCell.charAt(0) < idFirstCell.charAt(0) || idTargetCell.charAt(0) > idSecondCell.charAt(0))
                || idTargetCell.charAt(1) < idFirstCell.charAt(1) || idTargetCell.charAt(1) > idSecondCell.charAt(1)))
            throw new IllegalCommandException("Incorrect value. Cell cannot be inside a range");
    }

    private String[] getRangeArguments(Cell cell) throws IllegalCommandException {
        Pattern pattern = Pattern.compile("[A-Z][0-9]:[A-Z][0-9]");
        Matcher matcher = pattern.matcher(cell.getValue());
        if (matcher.find()) return matcher.group().split(":");
        else throw new IllegalCommandException("Incorrect value.Enter the correct value of the SUM formula");
    }
}
