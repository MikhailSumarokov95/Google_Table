package ru.sumarokov.google_table.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.dao.TableDAO;
import ru.sumarokov.google_table.models.formulas.*;

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

    /**
     * Принимает измененную ячейку, если в ячейку занесена формула вычисляет её,
     * записывает новое значение ячейки в БД и возвращает ячейку уже с новым значением.
     * В случае возникновении ошибки при обработке значения из ячейки
     * записывает описание ошибки в значение ячейки и возращает её, в БД ничего не отправлятся
     *
     * @param cell ячейка с данными для вычисления
     * @return ячейку с вычисленным значением или с описанием ошибки возникшей при вычислении
     */
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
