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
package com.github.luischavez.database.schema.component;

import com.github.luischavez.database.grammar.ColumnType;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class ColumnDefinition {

    private ColumnType columnType;
    private boolean nullable;
    private boolean unsigned;
    private boolean incremented;
    private int length;
    private int zeros;
    private Object defaultValue;

    public ColumnDefinition(ColumnType columnType) {
        this.columnType = columnType;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUnsigned() {
        return this.unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    public boolean isIncremented() {
        return this.incremented;
    }

    public void setIncremented(boolean incremented) {
        this.incremented = incremented;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getZeros() {
        return this.zeros;
    }

    public void setZeros(int zeros) {
        this.zeros = zeros;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
