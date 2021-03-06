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
package com.github.luischavez.database.query.component;

import com.github.luischavez.database.grammar.Component;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class HavingComponent implements Component {

    private final boolean and;
    private final String columnName;
    private final String operator;
    private final Object value;

    public HavingComponent(boolean and, String columnName, String operator, Object value) {
        this.and = and;
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    public boolean isAnd() {
        return this.and;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getOperator() {
        return this.operator;
    }

    public Object getValue() {
        return this.value;
    }
}
