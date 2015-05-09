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
import com.github.luischavez.database.grammar.Bindings;
import com.github.luischavez.database.grammar.Compilable;
import com.github.luischavez.database.grammar.ComponentBag;
import com.github.luischavez.database.grammar.JoinType;
import com.github.luischavez.database.grammar.SQLType;
import com.github.luischavez.database.handler.Handler;
import com.github.luischavez.database.link.Affecting;
import com.github.luischavez.database.link.Row;
import com.github.luischavez.database.link.RowList;
import com.github.luischavez.database.query.component.ColumnComponent;
import com.github.luischavez.database.query.component.DistinctComponent;
import com.github.luischavez.database.query.component.GroupComponent;
import com.github.luischavez.database.query.component.HavingComponent;
import com.github.luischavez.database.query.component.JoinComponent;
import com.github.luischavez.database.query.component.LimitComponent;
import com.github.luischavez.database.query.component.OffsetComponent;
import com.github.luischavez.database.query.component.OrderComponent;
import com.github.luischavez.database.query.component.TableComponent;
import com.github.luischavez.database.query.component.WhereComponent;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class Query implements Queryable<Query>, Compilable {

    private final ComponentBag componentBag;
    private final Bindings bindings;

    private final Handler handler;

    private SQLType type;

    public Query(Handler handler) {
        this.componentBag = new ComponentBag();
        this.bindings = new Bindings();
        this.handler = handler;
        this.type = SQLType.NONE;
    }

    protected void setColumns(String... columns) {
        this.componentBag.removeAll(ColumnComponent.class);
        for (String column : columns) {
            if (null != column && !column.isEmpty()) {
                this.componentBag.add(new ColumnComponent(column));
            }
        }
    }

    protected void selectRows(String... columns) {
        this.type = SQLType.SELECT;
        this.setColumns(columns);
    }

    protected void insertRows(String tableName, String columns, Object[][] values) {
        this.type = SQLType.INSERT;
        this.table(tableName).setColumns(columns);
        for (Object[] row : values) {
            this.bindings.set("values", row);
        }
    }

    protected void updateRows(String tableName, String columns, Object[] values) {
        this.type = SQLType.UPDATE;
        this.table(tableName).setColumns(columns);
        this.bindings.set("values", values);
    }

    protected void deleteRows(String tableName) {
        this.type = SQLType.DELETE;
        this.table(tableName);
    }

    @Override
    public Query table(String tableName) {
        this.componentBag.removeAll(TableComponent.class);
        if (null != tableName && !tableName.isEmpty()) {
            this.componentBag.add(new TableComponent(tableName));
        }
        return this;
    }

    @Override
    public Query distinct(boolean onlyDistinctResults) {
        this.componentBag.removeAll(DistinctComponent.class);
        if (onlyDistinctResults) {
            this.componentBag.add(new DistinctComponent());
        }
        return this;
    }

    @Override
    public Query join(String tableName, String firstColumn, String operator, String secondColumn) {
        this.componentBag.add(new JoinComponent(JoinType.INNER, tableName).on(firstColumn, operator, secondColumn));
        return this;
    }

    @Override
    public Query join(String tableName, Fluentable<JoinComponent> fluentable) {
        JoinComponent joinComponent = new JoinComponent(JoinType.INNER, tableName);
        fluentable.fluent(joinComponent);
        this.componentBag.add(joinComponent);
        return this;
    }

    @Override
    public Query naturalJoin(String tableName, String firstColumn, String operator, String secondColumn) {
        this.componentBag.add(new JoinComponent(JoinType.NATURAL, tableName).on(firstColumn, operator, secondColumn));
        return this;
    }

    @Override
    public Query naturalJoin(String tableName, Fluentable<JoinComponent> fluentable) {
        JoinComponent joinComponent = new JoinComponent(JoinType.NATURAL, tableName);
        fluentable.fluent(joinComponent);
        this.componentBag.add(joinComponent);
        return this;
    }

    @Override
    public Query leftJoin(String tableName, String firstColumn, String operator, String secondColumn) {
        this.componentBag.add(new JoinComponent(JoinType.LEFT, tableName).on(firstColumn, operator, secondColumn));
        return this;
    }

    @Override
    public Query leftJoin(String tableName, Fluentable<JoinComponent> fluentable) {
        JoinComponent joinComponent = new JoinComponent(JoinType.LEFT, tableName);
        fluentable.fluent(joinComponent);
        this.componentBag.add(joinComponent);
        return this;
    }

    @Override
    public Query rightJoin(String tableName, String firstColumn, String operator, String secondColumn) {
        this.componentBag.add(new JoinComponent(JoinType.RIGHT, tableName).on(firstColumn, operator, secondColumn));
        return this;
    }

    @Override
    public Query rightJoin(String tableName, Fluentable<JoinComponent> fluentable) {
        JoinComponent joinComponent = new JoinComponent(JoinType.RIGHT, tableName);
        fluentable.fluent(joinComponent);
        this.componentBag.add(joinComponent);
        return this;
    }

    @Override
    public Query fullJoin(String tableName, String firstColumn, String operator, String secondColumn) {
        this.componentBag.add(new JoinComponent(JoinType.FULL, tableName).on(firstColumn, operator, secondColumn));
        return this;
    }

    @Override
    public Query fullJoin(String tableName, Fluentable<JoinComponent> fluentable) {
        JoinComponent joinComponent = new JoinComponent(JoinType.FULL, tableName);
        fluentable.fluent(joinComponent);
        this.componentBag.add(joinComponent);
        return this;
    }

    @Override
    public Query where(String columnName, String operator, Object value) {
        this.componentBag.add(new WhereComponent(true, columnName, operator, value));
        this.bindings.set("wheres", value);
        return this;
    }

    @Override
    public Query orWhere(String columnName, String operator, Object value) {
        this.componentBag.add(new WhereComponent(false, columnName, operator, value));
        this.bindings.set("wheres", value);
        return this;
    }

    @Override
    public Query group(String columnName) {
        this.componentBag.add(new GroupComponent(columnName));
        return this;
    }

    @Override
    public Query having(String columnName, String operator, Object value) {
        this.componentBag.add(new HavingComponent(true, columnName, operator, value));
        this.bindings.set("havings", value);
        return this;
    }

    @Override
    public Query orHaving(String columnName, String operator, Object value) {
        this.componentBag.add(new HavingComponent(false, columnName, operator, value));
        this.bindings.set("havings", value);
        return this;
    }

    @Override
    public Query order(String columnName, boolean ascendant) {
        this.componentBag.add(new OrderComponent(columnName, ascendant));
        return this;
    }

    @Override
    public Query limit(int maxResults) {
        this.componentBag.removeAll(LimitComponent.class);
        this.componentBag.add(new LimitComponent(maxResults));
        return this;
    }

    @Override
    public Query offset(int firstResultIndex) {
        this.componentBag.removeAll(OffsetComponent.class);
        this.componentBag.add(new OffsetComponent(firstResultIndex));
        return this;
    }

    @Override
    public RowList get(String... columns) {
        this.selectRows(columns);
        return this.handler.fetch(this);
    }

    @Override
    public Row first(String... columns) {
        RowList rows = this.limit(1).get(columns);
        this.componentBag.removeAll(LimitComponent.class);
        return rows.empty() ? null : rows.getRow(0);
    }

    @Override
    public Affecting insert(String tableName, String columns, Object[][] values) {
        this.insertRows(tableName, columns, values);
        return this.handler.affect(this);
    }

    @Override
    public Affecting insert(String tableName, String columns, Object... values) {
        return this.insert(tableName, columns, new Object[][]{values});
    }

    @Override
    public Affecting update(String tableName, String columns, Object... values) {
        this.updateRows(tableName, columns, values);
        return this.handler.affect(this);
    }

    @Override
    public Affecting delete(String tableName) {
        this.deleteRows(tableName);
        return this.handler.affect(this);
    }

    @Override
    public SQLType type() {
        return this.type;
    }

    @Override
    public ComponentBag components() {
        return this.componentBag;
    }

    @Override
    public Bindings bindings() {
        return this.bindings;
    }
}
