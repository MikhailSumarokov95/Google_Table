package ru.sumarokov.google_table.models.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.Cell;

import java.util.List;

@Component
public class TableDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TableDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        createTable();
    }

    private void createTable() {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS google_table (id VARCHAR(2), val VARCHAR(310))");
        jdbcTemplate.update("ALTER TABLE google_table ADD UNIQUE (id)");
    }

    public String getValueCell(String id) {
        return getCell(id).getValue();
    }

    public Cell getCell(String id) {
        return jdbcTemplate.query("SELECT id, val FROM google_table WHERE id ILIKE ?",
                        new Object[] { id }, new CellMapper())
                .stream()
                .findAny()
                .orElseThrow(() -> new IndexOutOfBoundsException("The table does not contain such a cell"));
    }

    public void setValueCell(String id, String value) {
        jdbcTemplate.update
                ("INSERT INTO google_table(id, val) VALUES( upper(?), ?) ON CONFLICT (id) DO UPDATE SET val = EXCLUDED.val",
                        id, value);
    }

    public List<Cell> getCells() {
        return jdbcTemplate.query("SELECT id, val FROM google_table", new CellMapper());
    }

    public boolean hasCell(String id) {
        try {
            getCell(id);
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
        return true;
    }
}
