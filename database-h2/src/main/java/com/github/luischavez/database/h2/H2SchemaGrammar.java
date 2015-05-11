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
package com.github.luischavez.database.h2;

import com.github.luischavez.database.schema.SchemaGrammar;
import com.github.luischavez.database.schema.component.ColumnDefinition;
import com.github.luischavez.database.schema.component.ConstraintDefinition;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class H2SchemaGrammar extends SchemaGrammar {

    @Override
    protected String getAlterColumnString(String columnName, ColumnDefinition definition) {
        return super.getAlterColumnString(columnName, definition).replaceFirst("MODIFY", "ALTER");
    }

    @Override
    protected String getDropConstraintString(ConstraintDefinition definition) {
        return super.getDropConstraintString(definition).replaceFirst("UNIQUE", "CONSTRAINT");
    }
}
