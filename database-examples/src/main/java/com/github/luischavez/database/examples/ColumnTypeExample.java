/* 
 * Copyright (C) 2015 Luis Chávez
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.luischavez.database.examples;

import com.github.luischavez.database.Database;
import com.github.luischavez.database.link.Row;

import java.math.BigDecimal;

import java.nio.ByteBuffer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class ColumnTypeExample implements Example {

    private static final String TABLE_NAME = "columns";

    protected void createTables(Database database) {
        database.create(TABLE_NAME, table -> {
            table.bool("boolean_column").defaults(true);
            table.date("date_column").defaults(LocalDate.now());
            table.time("time_column").defaults(LocalTime.now());
            table.dateTime("datetime_column").defaults(LocalDateTime.now());
            table.string("string_column", 50).defaults("Luis");
            table.text("text_column");
            table.integer("integer_column", 10).unsigned().defaults(5);
            table.decimal("decimal_column", 10, 0).unsigned().defaults(BigDecimal.valueOf(10));
            table.binary("binary_column");
        });
    }

    protected void dropTables(Database database) {
        database.drop(TABLE_NAME);
    }

    @Override
    public void execute(Database database) {
        this.createTables(database);

        database.insert(TABLE_NAME, "text_column, binary_column",
                "lorem ipsum etc etc", ByteBuffer.wrap(new byte[]{0, 1, 1, 1, 0}));

        Row columns = database.table(TABLE_NAME).first();
        String[] keys = columns.keys();
        for (String key : keys) {
            Object value = columns.value(key);
            this.log("{}: {} [{}]", key, value, value.getClass());
        }

        this.dropTables(database);
    }
}
