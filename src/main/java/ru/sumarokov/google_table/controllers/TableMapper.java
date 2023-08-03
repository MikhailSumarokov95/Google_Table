package ru.sumarokov.google_table.controllers;

import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.Cell;
import java.util.*;

/**
 *  Данный класс необходим для передачи данных о таблице между контроллером и представлением(view)
 */
@Component
public class TableMapper {

    private final List<Cell> cells;

    private final Cell a1 = new Cell("A", "1");
    private final Cell b1 = new Cell("B", "1");
    private final Cell c1 = new Cell("C", "1");
    private final Cell d1 = new Cell("D", "1");
    private final Cell a2 = new Cell("A", "2");
    private final Cell b2 = new Cell("B", "2");
    private final Cell c2 = new Cell("C", "2");
    private final Cell d2 = new Cell("D", "2");
    private final Cell a3 = new Cell("A", "3");
    private final Cell b3 = new Cell("B", "3");
    private final Cell c3 = new Cell("C", "3");
    private final Cell d3 = new Cell("D", "3");
    private final Cell a4 = new Cell("A", "4");
    private final Cell b4 = new Cell("B", "4");
    private final Cell c4 = new Cell("C", "4");
    private final Cell d4 = new Cell("D", "4");

    public TableMapper() {
        cells = new ArrayList<>();
        cells.add(a1);
        cells.add(b1);
        cells.add(c1);
        cells.add(d1);
        cells.add(a2);
        cells.add(b2);
        cells.add(c2);
        cells.add(d2);
        cells.add(a3);
        cells.add(b3);
        cells.add(c3);
        cells.add(d3);
        cells.add(a4);
        cells.add(b4);
        cells.add(c4);
        cells.add(d4);
    }

    // Через данные методы таблица в table.html получает значения для заполнения ячеек
    public Cell getA1() {
        return a1;
    }

    public Cell getB1() {
        return b1;
    }

    public Cell getC1() { return c1; }

    public Cell getD1() { return d1; }

    public Cell getA2() {
        return a2;
    }

    public Cell getB2() {
        return b2;
    }

    public Cell getC2() {
        return c2;
    }

    public Cell getD2() {
        return d2;
    }

    public Cell getA3() {
        return a3;
    }

    public Cell getB3() {
        return b3;
    }

    public Cell getC3() {
        return c3;
    }

    public Cell getD3() {
        return d3;
    }

    public Cell getA4() {
        return a4;
    }

    public Cell getB4() {
        return b4;
    }

    public Cell getC4() {
        return c4;
    }

    public Cell getD4() {
        return d4;
    }

    /**
     * Находит ячейку в которой были переданны данные из представления(view)
     * При создании объекта TableMapper в представлении, значение заполняется
     * только у ячейки в которую были введены данные
     * остальные ячейки заполняются дефолтными значением
     * Этим и пользуется данный метод для нахождения ячейки с измененным значением
     * @return ячейку в которую было внесено новое значение
     */
    public Cell getChangedCell() {
        for (int i = 0; i < cells.size(); i++)
            if (!cells.get(i).getValue().equals(cells.get(i).getDefaultValue()))
                return cells.get(i);
        return null;
    }

    public void setCell(Cell cell) {
        for (int i = 0; i < cells.size(); i++)
            if (cells.get(i).equalsId(cell.getId()))
                cells.get(i).setValue(cell.getValue());
    }

    public void setCells(List<Cell> cells) {
        if (cells == null) return;
        for (Cell cell : cells)
            setCell(cell);
    }

    @Override
    public String toString() {
        StringBuilder cellsStr = new StringBuilder();
        for (Cell cell : cells)
            cellsStr.append(cell.toString());
        return cellsStr.toString();
    }
}
