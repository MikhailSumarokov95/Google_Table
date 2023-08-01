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

    @GetMapping()
    public String initTable(Model model) {
        model.addAttribute("table", tableMapper);
        return "table/table";
    }

    @PatchMapping()
    public String refreshTable(@ModelAttribute("table") TableMapper tableChanged) {
        Cell cell = tableChanged.getChangedCell();
        System.out.println("cell is changed: " + cell);
        System.out.println("tableChanged Do not Changed: " + tableChanged);
        if (cell != null) {
            Cell newCell = tableModel.changeTable(cell);
            tableMapper.setCell(newCell);
            System.out.println("tableMapper Changed: " + tableMapper);
        }
        return "redirect:/table";
    }
}
