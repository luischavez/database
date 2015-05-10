/*
 * Copyright (C) 2015 Luis Chávez <https://github.com/luischavez>
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
package com.github.luischavez.database.schema.component;

import com.github.luischavez.database.grammar.Component;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class CreateColumnComponent implements Component {

    private final String columnName;
    private final ColumnDefinition definition;

    private boolean alter;

    public CreateColumnComponent(String columnName, ColumnDefinition definition) {
        this.columnName = columnName;
        this.definition = definition;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public ColumnDefinition getDefinition() {
        return this.definition;
    }

    public boolean isAlter() {
        return this.alter;
    }

    public void setAlter(boolean alter) {
        this.alter = alter;
    }
}
