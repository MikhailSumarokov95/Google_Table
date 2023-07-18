package ru.sumarokov.google_table.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TableModel {

    private final ValueParser valueParser;
    private final Calculator calculator;
    private final Validator validator;

    @Autowired
    public TableModel(ValueParser valueParser, Calculator calculator, Validator validator) {
        this.valueParser = valueParser;
        this.calculator = calculator;
        this.validator = validator;
    }

    public void changeTable(Table table, Cell cell) {
        String value = cell.getValue();

        try {
            validator.validate(value);

            if (valueParser.isFormula(value)) {
                value = valueParser.parseFormula(value, table);
                value = calculator.calculate(value);
            }
            else value = String.valueOf(Double.parseDouble(value));

        } catch (Exception ex) {
            value = ex.getMessage();
        }
        table.getCell(cell.getId()).setValue(value);
    }
}
