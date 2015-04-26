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
package com.github.luischavez.database.h2;

import com.github.luischavez.database.Support;
import com.github.luischavez.database.jdbc.JDBCTransform;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class H2Support extends Support {

    public H2Support() {
        super(new H2Linker(), new H2QueryGrammar(), new H2SchemaGrammar(), new JDBCTransform());
    }
}
