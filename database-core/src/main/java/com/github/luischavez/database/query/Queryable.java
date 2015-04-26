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
package com.github.luischavez.database.query;

import com.github.luischavez.database.function.Fluentable;
import com.github.luischavez.database.query.component.JoinComponent;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 * @param <Q>
 */
public interface Queryable<Q extends Queryable<Q>> {

    public Q distinct(boolean onlyDistinctResults);

    public Q column(String columnName);

    public Q join(String tableName, String firstColumn, String operator, String secondColumn);

    public Q join(String tableName, Fluentable<JoinComponent> fluentable);

    public Q naturalJoin(String tableName, String firstColumn, String operator, String secondColumn);

    public Q naturalJoin(String tableName, Fluentable<JoinComponent> fluentable);

    public Q leftJoin(String tableName, String firstColumn, String operator, String secondColumn);

    public Q leftJoin(String tableName, Fluentable<JoinComponent> fluentable);

    public Q rightJoin(String tableName, String firstColumn, String operator, String secondColumn);

    public Q rightJoin(String tableName, Fluentable<JoinComponent> fluentable);

    public Q fullJoin(String tableName, String firstColumn, String operator, String secondColumn);

    public Q fullJoin(String tableName, Fluentable<JoinComponent> fluentable);

    public Q where(String columnName, String operator, Object value);

    public Q orWhere(String columnName, String operator, Object value);

    public Q group(String columnName);

    public Q having(String columnName, String operator, Object value);

    public Q orHaving(String columnName, String operator, Object value);

    public Q order(String columnName, boolean ascendant);

    public Q limit(int maxResults);

    public Q offset(int firstResultIndex);
}
