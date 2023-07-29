package ru.sumarokov.google_table.models.furmulas;

import java.util.List;

public class Formula {

    public final FormulaType type;
    public final List<String> args;

    public Formula(FormulaType type, List<String> args) {
        this.type = type;
        this.args = args;
    }

    public FormulaType getType() {
        return type;
    }

    public List<String> getArgs() {
        return args;
    }
}
