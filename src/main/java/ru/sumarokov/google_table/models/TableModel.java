package ru.sumarokov.google_table.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.dao.TableDAO;
import ru.sumarokov.google_table.models.furmulas.*;

@Component
public class TableModel {

    private TableDAO tableDAO;
    private final Parser parser;
    private final Calculator calculator;

    @Autowired
    public TableModel(Parser parser, Calculator calculator, TableDAO tableDAO) {
        this.parser = parser;
        this.calculator = calculator;
        this.tableDAO = tableDAO;
    }

    public Cell changeTable(Cell cell) {
        String value = "";
        try {
            Formula formula = parser.parse(tableDAO, cell);
            value = calculator.calculate(formula);
            tableDAO.setValueCell(cell.getId(), value);
        } catch (IllegalCommandException ex) {
            value = ex.getMessage();
        }
        cell.setValue(value);
        return cell;
    }
}
