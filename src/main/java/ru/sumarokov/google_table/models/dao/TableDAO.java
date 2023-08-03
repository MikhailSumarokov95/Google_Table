package ru.sumarokov.google_table.models.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import ru.sumarokov.google_table.models.Cell;
import ru.sumarokov.google_table.models.IllegalCommandException;

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
        // Создание таблицы "google_table" если такой нету
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS google_table (id VARCHAR(2), val VARCHAR(310))");
        // Защита стобца "id" от повторения
        jdbcTemplate.update("ALTER TABLE google_table ADD UNIQUE (id)");
    }

    /**
     * Возвращает значение ячейки из БД по id
     *
     * @param id (к регистру не чувствительно) ячейки значение которой нужно получить
     * @return значение ячейки найденной по id
     * @throws IndexOutOfBoundsException в случае если ячейки с данным id не существует в БД
     */
    public String getValueCell(String id) {
        return getCell(id).getValue();
    }

    /**
     * Возвращает объект ячейки из БД с указанным id
     *
     * @param id (к регистру не чувствительно) ячейки которую хотим получить
     * @return объект ячейки найденной по id
     * @throws IndexOutOfBoundsException в случае если ячейка с данным id не существует в БД
     */
    public Cell getCell(String id) {
        return jdbcTemplate.query("SELECT id, val FROM google_table WHERE id ILIKE ?",
                        new Object[]{id}, new CellMapper())
                .stream()
                .findAny()
                .orElseThrow(() -> new IndexOutOfBoundsException("The table does not contain such a cell"));
    }

    /**
     * Записывает в БД значение ячейки в строку с заданным id.
     * Если такой строки нет, то она создается,
     * перед созданием новой ячейки id переводится в верхний регистр
     *
     * @param id    ячейки
     * @param value ячейки
     */
    public void setValueCell(String id, String value) {
        jdbcTemplate.update
                ("INSERT INTO google_table(id, val) VALUES( upper(?), ?) ON CONFLICT (id) DO UPDATE SET val = EXCLUDED.val",
                        id, value);
    }

    /**
     * Возвращает все ячейки хранящиеся в БД
     *
     * @return список объектов ячеек
     */
    public List<Cell> getCells() {
        return jdbcTemplate.query("SELECT id, val FROM google_table", new CellMapper());
    }

    /**
     * Определяет хранится ли ячейка с данным id в БД
     *
     * @param id ячейки
     * @return true - если ячейка существует БД, false - если ячейки нет в БД
     */
    public boolean hasCell(String id) {
        try {
            getCell(id);
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
        return true;
    }
}
