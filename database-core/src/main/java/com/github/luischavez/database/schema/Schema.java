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
package com.github.luischavez.database.schema;

import com.github.luischavez.database.grammar.ColumnType;
import com.github.luischavez.database.schema.component.ColumnDefinition;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 * @param <T>
 */
public class Schema<T> {

    private final ColumnDefinition definition;

    public Schema(ColumnType columnType) {
        this.definition = new ColumnDefinition(columnType);
    }

    public Schema<T> nullable() {
        this.definition.setNullable(true);
        return this;
    }

    public Schema<T> unsigned() {
        this.definition.setUnsigned(true);
        return this;
    }

    public Schema<T> incremented() {
        this.definition.setIncremented(true);
        return this;
    }

    public Schema<T> defaults(T defaultValue) {
        this.definition.setDefaultValue(defaultValue);
        return this;
    }

    public ColumnDefinition getDefinition() {
        return this.definition;
    }
}
