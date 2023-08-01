package ru.sumarokov.google_table.models.dao;

import ru.sumarokov.google_table.models.Cell;
import org.springframework.jdbc.core.RowMapper;
import java.sql.*;

public class CellMapper implements RowMapper<Cell> {

    @Override
    public Cell mapRow(ResultSet resultSet, int i) throws SQLException {
        String id = resultSet.getString("id");
        String value = resultSet.getString("val");
        String column = String.valueOf(id.charAt(0));
        String line = String.valueOf(id.charAt(1));
        Cell cell = new Cell (column, line, value);
        return cell;
    }
}
