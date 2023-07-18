package ru.sumarokov.google_table.models;

public class Cell {

    private final String line;
    private final String column;
    private final String id;
    private final String defaultValue = "";
    private String value;


    public Cell(String column, String line) {
        this.line = line;
        this.column = column;
        this.id = column + line;
        this.value = defaultValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLine() {
        return line;
    }

    public String getColumn() {
        return column;
    }

    public String getId() { return id;}

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return "Cell{" +
                ", id='" + id + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
