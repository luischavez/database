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
package com.github.luischavez.database.query.component;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class JoinClause {

    private final boolean and;
    private final String firstColumn;
    private final String operator;
    private final String secondColumn;

    public JoinClause(boolean and, String firstColumn, String operator, String secondColumn) {
        this.and = and;
        this.firstColumn = firstColumn;
        this.operator = operator;
        this.secondColumn = secondColumn;
    }

    public boolean isAnd() {
        return and;
    }

    public String getFirstColumn() {
        return this.firstColumn;
    }

    public String getOperator() {
        return this.operator;
    }

    public String getSecondColumn() {
        return this.secondColumn;
    }
}
