package ru.sumarokov.google_table.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sumarokov.google_table.models.Cell;
import ru.sumarokov.google_table.models.Table;
import ru.sumarokov.google_table.models.TableModel;

@Controller
@RequestMapping("/table")
public class TableController {

    private final Table table;
    private final TableModel tableModel;

    @Autowired
    public TableController(Table table, TableModel tableModel) {
        this.table = table;
        this.tableModel = tableModel;
    }

    @GetMapping()
    public String initTable(Model model) {
        model.addAttribute("table", table);
        return "table/table";
    }

    @PatchMapping()
    public String refreshTable(@ModelAttribute("table") Table table) {
        Cell cell = table.getChangedCell();
        if (cell != null) tableModel.changeTable(this.table, cell);
        return "redirect:/table";
    }
}
