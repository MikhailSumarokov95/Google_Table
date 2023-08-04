package ru.sumarokov.google_table.models;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.sumarokov.google_table.models.*;
import ru.sumarokov.google_table.models.dao.TableDAO;
import ru.sumarokov.google_table.models.furmulas.*;
import java.util.*;

public class ParserTest {

    private final Validator validator = new Validator();
    private final Parser parser = new Parser(validator);
    private final static String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private final static String URL = "jdbc:postgresql://localhost:5432/test";
    private final static String USERNAME = "postgres";
    private final static String PASSWORD = "123";


    @Test
    public void parseSimpleNumber() {
        testNumber("12345678987654321");
    }

    @Test
    public void parseDoubleNumber() {
        testNumber("12345678987654321.000");
    }

    @Test
    public void parseExpressionWithCellReferences() {
        String expression = "=1+2*(3+B3/2-(C3+-2))*2+1+(20.3+13)*(1+21)";
        TableDAO tableDAO = getCorrectTable();
        List<String> tokens = Arrays.asList(
                "1", "+", "2", "*", "(", "3",
                "+", "0", "/", "2", "-", "(",
                "-0.12333", "+", "-2", ")", ")", "*",
                "2", "+", "1", "+", "(", "20.3", "+",
                "13", ")", "*", "(", "1", "+", "21", ")");

        testParse(expression, tokens, tableDAO, FormulaType.Expression);
    }

    @Test
    public void parseExpression() {
        String expression = "=1+2*(3+312/2-(1+-2))*2+1+(20.3+13)*(1+21)";
        TableDAO tableDAO = getCorrectTable();
        List<String> tokens = Arrays.asList(
                "1", "+", "2", "*", "(", "3",
                "+", "312", "/", "2", "-", "(",
                "1", "+", "-2", ")", ")", "*",
                "2", "+", "1", "+", "(", "20.3", "+",
                "13", ")", "*", "(", "1", "+", "21", ")");

        testParse(expression, tokens, tableDAO, FormulaType.Expression);
    }

    @Test
    public void parseSumFormulaColumn() {
        String expression = "SUM(B1:B3)";
        TableDAO tableDAO = getCorrectTable();
        List<String> tokens = Arrays.asList(
                "21333333333333333333", "21333333333333333333", "0");
        testParse(expression, tokens, tableDAO, FormulaType.Sum);
    }

    @Test
    public void parseSumFormulaLine() {
        String expression = "SUM(A2:C2)";
        TableDAO tableDAO = getCorrectTable();
        List<String> tokens = Arrays.asList(
                "0.12333333333333333333", "21333333333333333333", "-1233333333333333333333333");
        testParse(expression, tokens, tableDAO, FormulaType.Sum);
    }

    @Test
    public void parseSumFormulaBeginningAndEndSapped() {
        String expression = "SUM(C2:A2)";
        TableDAO tableDAO = getCorrectTable();
        List<String> tokens = Arrays.asList(
                "0.12333333333333333333", "21333333333333333333", "-1233333333333333333333333");
        testParse(expression, tokens, tableDAO, FormulaType.Sum);
    }

    private void testNumber(String value) {
        TableDAO tableDAO = getCorrectTable();
        testParse(value, Arrays.asList(value), tableDAO, FormulaType.Number);
    }

    private void testParse(String value, List<String> args, TableDAO tableDAO, FormulaType formulaType) {
        Formula formula = new Formula(formulaType, args);
        Cell cell = new Cell ("A", "1", value);

        try {
            Assertions.assertEquals(formula, parser.parse(tableDAO, cell));
        } catch (IllegalCommandException ex) {
            ex.printStackTrace();
        }
    }

    private TableDAO getCorrectTable() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        TableDAO table = new TableDAO(new JdbcTemplate(dataSource));
        table.setValueCell("A1", "1");
        table.setValueCell("A2", "0.12333333333333333333");
        table.setValueCell("A3", "321");
        table.setValueCell("B1", "21333333333333333333");
        table.setValueCell("B2", "21333333333333333333");
        table.setValueCell("B3", "0");
        table.setValueCell("C1", "-1");
        table.setValueCell("C2", "-1233333333333333333333333");
        table.setValueCell("C3", "-0.12333");
        return table;
    }
}
