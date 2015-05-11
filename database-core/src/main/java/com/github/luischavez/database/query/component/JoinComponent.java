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

import com.github.luischavez.database.grammar.JoinType;
import com.github.luischavez.database.grammar.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class JoinComponent implements Component {

    private final JoinType type;
    private final String tableName;
    private final List<JoinClause> clauses;

    public JoinComponent(JoinType type, String tableName) {
        this.type = type;
        this.tableName = tableName;
        this.clauses = new ArrayList<>();
    }

    public JoinType getType() {
        return this.type;
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<JoinClause> getClauses() {
        return this.clauses;
    }

    public JoinComponent on(String firstColumn, String operator, String secondColumn) {
        this.clauses.add(new JoinClause(true, firstColumn, operator, secondColumn));
        return this;
    }

    public JoinComponent or(String firstColumn, String operator, String secondColumn) {
        this.clauses.add(new JoinClause(false, firstColumn, operator, secondColumn));
        return this;
    }
}
