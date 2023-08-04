package ru.sumarokov.google_table.models.formulas;

import java.util.List;
import java.util.Objects;

public class Formula {

    private final FormulaType type;
    private final List<String> args;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Formula formula = (Formula) o;
        return type == formula.type && Objects.equals(args, formula.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, args);
    }
}
