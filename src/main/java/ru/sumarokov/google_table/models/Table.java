package ru.sumarokov.google_table.models;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Table {

    private final List<Cell> cells;
    private final Cell a1 = new Cell("a", "1");
    private final Cell b1 = new Cell("b", "1");
    private final Cell c1 = new Cell("c", "1");
    private final Cell d1 = new Cell("d", "1");
    private final Cell a2 = new Cell("a", "2");
    private final Cell b2 = new Cell("b", "2");
    private final Cell c2 = new Cell("c", "2");
    private final Cell d2 = new Cell("d", "2");
    private final Cell a3 = new Cell("a", "3");
    private final Cell b3 = new Cell("b", "3");
    private final Cell c3 = new Cell("c", "3");
    private final Cell d3 = new Cell("d", "3");
    private final Cell a4 = new Cell("1", "4");
    private final Cell b4 = new Cell("b", "4");
    private final Cell c4 = new Cell("c", "4");
    private final Cell d4 = new Cell("d", "4");

    public Table() {
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

    public Cell getA1() {
        return a1;
    }

    public Cell getB1() {
        return b1;
    }

    public Cell getC1() {
        return c1; }

    public Cell getD1() {
        return d1; }

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

    public Cell getChangedCell() {
        for (int i = 0; i < cells.size(); i++)
            if (!cells.get(i).getValue().equals(cells.get(i).getDefaultValue()))
                return cells.get(i);
        return null;
    }

    public String getValueCell(String id) {//
        for (int i = 0; i < cells.size(); i++)
            if (cells.get(i).getId().equals(id))
                return cells.get(i).getValue();
        return null;
    }

    public Cell getCell(String id) {
        for (int i = 0; i < cells.size(); i++)
            if (cells.get(i).getId().equals(id))
                return cells.get(i);
        return null;
    }

    @Override
    public String toString() {
        return "Table{" +
                "cells=" + cells +
                '}';
    }
}