package ru.sumarokov.google_table.models.formulas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sumarokov.google_table.models.*;

import java.util.*;

public class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    public void calculateExpression() {
        List<String> tokens = Arrays.asList(
                "1", "+", "2", "*", "(", "3",
                "+", "312", "/", "2", "-", "(",
                "1", "+", "-2", ")", ")", "*",
                "2", "+", "1", "+", "(", "20.3", "+",
                "13", ")", "*", "(", "1", "+", "21", ")");
        String result = "1374.6";
        testCalculate(tokens, result, FormulaType.Expression);
    }

    @Test
    public void calculateSumFormula() {
        List<String> tokens = Arrays.asList("-20", "30.5", "0.5", "1100");
        String result = "1111.0";
        testCalculate(tokens, result, FormulaType.Sum);
    }

    @Test
    public void calculateNumber() {
        List<String> tokens = Arrays.asList("-20");
        String result = "-20.0";
        testCalculate(tokens, result, FormulaType.Sum);
    }

    private void testCalculate(List<String> args, String result, FormulaType formulaType) {
        Formula formula = new Formula(formulaType, args);
        try {
            Assertions.assertEquals(result, calculator.calculate(formula));
        } catch (IllegalCommandException ex) {
            ex.printStackTrace();
        }
    }
}
