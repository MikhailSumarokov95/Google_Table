package ru.sumarokov.google_table.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.furmulas.*;

@Component
public class TableModel {

    private final Parser parser;
    private final Calculator calculator;

    @Autowired
    public TableModel(Parser parser, Calculator calculator) {
        this.parser = parser;
        this.calculator = calculator;
    }

    public void changeTable(Table table, Cell cell) {
        String value = "";
        try {
            Formula formula = parser.parse(table, cell);
            value = calculator.calculate(formula);
        } catch (IllegalCommandException ex) {
            value = ex.getMessage();
        }
        table.getCell(cell.getId()).setValue(value);
    }
}
