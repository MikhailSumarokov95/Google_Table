package ru.sumarokov.google_table.models;

import org.springframework.stereotype.Component;

@Component
public class ValueParser {

    public String parseFormula(String value, Table table) {
        StringBuilder formula = new StringBuilder();
        for (int i = 1; i < value.length(); i++) {
            if (Character.isLetter(value.charAt(i)))
            {
                String id = (String.valueOf(value.charAt(i)) + value.charAt(i + 1)).toLowerCase();
                String valueCell = table.getValueCell(id);
                formula.append(valueCell);
            }
            else if (i != 1 && Character.isLetter(value.charAt(i - 1))) ;
            else formula.append(value.charAt(i));
        }
        return formula.toString();
    }

    public boolean isFormula(String value) {
        return value.charAt(0) == '=';
    }
}
