package ru.sumarokov.google_table.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sumarokov.google_table.models.Cell;
import ru.sumarokov.google_table.models.TableModel;
import ru.sumarokov.google_table.models.dao.TableDAO;

@Controller
@RequestMapping("/table")
public class TableController {

    private final TableMapper tableMapper;
    private final TableModel tableModel;

    @Autowired
    public TableController(TableMapper tableMapper, TableModel tableModel, TableDAO tableDAO) {
        this.tableMapper = tableMapper;
        tableMapper.setCells(tableDAO.getCells());
        this.tableModel = tableModel;
    }

    /**
     * Инициализирует представление(view)
     */
    @GetMapping()
    public String initTable(Model model) {
        model.addAttribute("table", tableMapper);
        return "table/table";
    }

    /**
     * Получает объект класса TableMapper и находит в нем измененную ячейку
     * Обращается к TableModel для вычисления значения из данных полученных в измененной ячейки
     * Затем обновляется представление(view) с новыми данными
     */
    @PatchMapping()
    public String refreshTable(@ModelAttribute("table") TableMapper tableChanged) {
        Cell cell = tableChanged.getChangedCell();
        if (cell != null) {
            Cell newCell = tableModel.changeTable(cell);
            tableMapper.setCell(newCell);
        }
        return "redirect:/table";
    }
}
