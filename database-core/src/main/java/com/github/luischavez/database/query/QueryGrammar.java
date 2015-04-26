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

import com.github.luischavez.database.grammar.JoinType;
import com.github.luischavez.database.grammar.SQLType;
import com.github.luischavez.database.grammar.Bindings;
import com.github.luischavez.database.grammar.CompilerException;
import com.github.luischavez.database.grammar.ComponentBag;
import com.github.luischavez.database.grammar.Grammar;
import com.github.luischavez.database.query.component.ColumnComponent;
import com.github.luischavez.database.query.component.DistinctComponent;
import com.github.luischavez.database.query.component.GroupComponent;
import com.github.luischavez.database.query.component.HavingComponent;
import com.github.luischavez.database.query.component.JoinClause;
import com.github.luischavez.database.query.component.JoinComponent;
import com.github.luischavez.database.query.component.LimitComponent;
import com.github.luischavez.database.query.component.OffsetComponent;
import com.github.luischavez.database.query.component.OrderComponent;
import com.github.luischavez.database.query.component.TableComponent;
import com.github.luischavez.database.query.component.WhereComponent;

import java.util.List;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class QueryGrammar extends Grammar {

    protected String getOperatorString(String operator) {
        return operator;
    }

    protected String getValueString(Object value) {
        if (value instanceof Object[]) {
            Object[] values = Object[].class.cast(value);

            StringBuilder builder = new StringBuilder();

            builder.append("(");
            for (int i = 0; i < values.length; i++) {
                builder.append(",?");
            }
            builder.append(")");

            return builder.toString().replaceFirst("\\(,", "\\(");
        }

        return "?";
    }

    protected String getJoinTypeString(JoinType type) {
        return type.name();
    }

    protected String dot(String string) {
        if (string.contains(".")) {
            String[] dot = string.split("\\.", 2);

            return this.wrap(dot[0]) + "." + this.wrap(dot[1]);
        }

        return this.wrap(string);
    }

    protected String alias(String string) {
        if (string.contains(" ")) {
            String[] alias = string.split(" ", 2);

            return this.dot(alias[0]) + " " + this.dot(alias[1]);
        }

        return this.dot(string);
    }

    protected String as(String string) {
        if (string.contains(" AS ")) {
            String[] as = string.split(" AS ", 2);

            return this.alias(as[0]) + " AS " + this.alias(as[1]);
        }

        return this.alias(string);
    }

    protected String[] split(String columns) {
        String[] split = columns.split(",");

        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }

        return split;
    }

    protected String join(String[] columns) {
        StringBuilder builder = new StringBuilder();

        for (String column : columns) {
            builder.append(",").append(column);
        }

        return builder.toString().replaceFirst(",", "");
    }

    protected String escape(String string) {
        if ("*".equals(string)) {
            return string;
        }

        String[] segments = this.split(string);

        for (int i = 0; i < segments.length; i++) {
            segments[i] = this.as(segments[i]);
        }

        return this.join(segments);
    }

    protected String compileDistinct(DistinctComponent distinctComponent) {
        return null != distinctComponent ? "DISTINCT" : "";
    }

    protected String compileColumns(List<ColumnComponent> columnComponents) {
        if (columnComponents.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (ColumnComponent columnComponent : columnComponents) {
            String columnName = columnComponent.getColumnName();
            builder.append(",").append(this.escape(columnName));
        }

        return builder.toString().replaceFirst(",", "");
    }

    protected String compileInsertColumns(List<ColumnComponent> columnComponents) {
        if (columnComponents.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (ColumnComponent columnComponent : columnComponents) {
            String columnName = columnComponent.getColumnName();

            for (String column : this.split(columnName)) {
                builder.append(",").append(this.wrap(column));
            }
        }

        return builder.toString().replaceFirst(",", "");
    }

    protected String compileUpdateColumns(List<ColumnComponent> columnComponents) {
        if (columnComponents.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("SET ");
        for (ColumnComponent columnComponent : columnComponents) {
            String columnName = columnComponent.getColumnName();
            builder.append(",")
                    .append(this.wrap(columnName))
                    .append(" = ?");
        }

        return builder.toString().replaceFirst(",", "");
    }

    protected String compileTable(TableComponent tableComponent) {
        if (null == tableComponent) {
            throw new CompilerException("Undefined table");
        }

        String tableName = tableComponent.getTableName();

        return this.escape(tableName);
    }

    protected String compileJoins(List<JoinComponent> joinComponents) {
        if (joinComponents.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (JoinComponent joinComponent : joinComponents) {
            List<JoinClause> clauses = joinComponent.getClauses();
            if (clauses.isEmpty()) {
                continue;
            }

            JoinType type = joinComponent.getType();
            String tableName = joinComponent.getTableName();

            builder.append(this.getJoinTypeString(type))
                    .append(" JOIN ")
                    .append(this.escape(tableName));

            for (JoinClause clause : clauses) {
                boolean and = clause.isAnd();
                String firstColumn = clause.getFirstColumn();
                String operator = clause.getOperator();
                String secondColumn = clause.getSecondColumn();

                builder.append(and ? " ON " : " OR ")
                        .append(this.escape(firstColumn))
                        .append(" ").append(this.getOperatorString(operator))
                        .append(" ").append(this.escape(secondColumn));
            }

            builder.append(" ");
        }

        return builder.toString().trim();
    }

    protected String compileWheres(List<WhereComponent> whereComponents) {
        if (whereComponents.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (WhereComponent whereComponent : whereComponents) {
            boolean and = whereComponent.isAnd();
            String columnName = whereComponent.getColumnName();
            String operator = whereComponent.getOperator();
            Object value = whereComponent.getValue();

            builder.append(and ? " AND " : " OR ")
                    .append(this.escape(columnName))
                    .append(" ").append(this.getOperatorString(operator))
                    .append(" ").append(this.getValueString(value));
        }

        String wheres = builder.toString().replaceFirst("AND |OR ", "").trim();

        return "WHERE " + wheres;
    }

    protected String compileHavings(List<HavingComponent> havingComponents) {
        if (havingComponents.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (HavingComponent havingComponent : havingComponents) {
            boolean and = havingComponent.isAnd();
            String columnName = havingComponent.getColumnName();
            String operator = havingComponent.getOperator();
            Object value = havingComponent.getValue();

            builder.append(and ? " AND " : " OR ")
                    .append(this.escape(columnName))
                    .append(" ").append(this.getOperatorString(operator))
                    .append(" ").append(this.getValueString(value));
        }

        String wheres = builder.toString().replaceFirst("AND |OR ", "").trim();

        return "HAVING " + wheres;
    }

    protected String compileGroups(List<GroupComponent> groupComponents) {
        if (groupComponents.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (GroupComponent groupComponent : groupComponents) {
            String columnName = groupComponent.getColumnName();
            builder.append(",").append(columnName);
        }

        String groups = builder.toString().replaceFirst(",", "");

        return "GROUP BY " + this.escape(groups);
    }

    protected String compileOrders(List<OrderComponent> orderComponents) {
        if (orderComponents.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (OrderComponent orderComponent : orderComponents) {
            String columnName = orderComponent.getColumnName();
            boolean ascendant = orderComponent.isAscendant();

            builder.append(",").append(this.escape(columnName))
                    .append(" ").append(ascendant ? "ASC" : "DESC");
        }

        String orders = builder.toString().replaceFirst(",", "");

        return "ORDER BY " + orders;
    }

    protected String compileLimit(LimitComponent limitComponent) {
        if (null == limitComponent) {
            return "";
        }

        return "LIMIT " + limitComponent.getMaxResults();
    }

    protected String compileOffset(OffsetComponent offsetComponent) {
        if (null == offsetComponent) {
            return "";
        }

        return "OFFSET " + offsetComponent.getFirstResultIndex();
    }

    protected String compileSelect(ComponentBag componentBag) {
        TableComponent tableComponent = componentBag.getFirst(TableComponent.class);
        LimitComponent limitComponent = componentBag.getFirst(LimitComponent.class);
        OffsetComponent offsetComponent = componentBag.getFirst(OffsetComponent.class);

        if (null != offsetComponent && null == limitComponent) {
            throw new CompilerException("Can't create offset without limit");
        }

        DistinctComponent distinctComponent = componentBag.getFirst(DistinctComponent.class);
        List<ColumnComponent> columnComponents = componentBag.getAll(ColumnComponent.class);
        List<JoinComponent> joinComponents = componentBag.getAll(JoinComponent.class);
        List<WhereComponent> whereComponents = componentBag.getAll(WhereComponent.class);
        List<GroupComponent> groupComponents = componentBag.getAll(GroupComponent.class);
        List<HavingComponent> havingComponents = componentBag.getAll(HavingComponent.class);
        List<OrderComponent> orderComponents = componentBag.getAll(OrderComponent.class);

        String distinct = this.compileDistinct(distinctComponent);
        String columns = this.compileColumns(columnComponents);

        if (columns.isEmpty()) {
            columns = "*";
        }

        String table = this.compileTable(tableComponent);
        String joins = this.compileJoins(joinComponents);
        String wheres = this.compileWheres(whereComponents);
        String groups = this.compileGroups(groupComponents);
        String havings = this.compileHavings(havingComponents);
        String orders = this.compileOrders(orderComponents);
        String limit = this.compileLimit(limitComponent);
        String offset = this.compileOffset(offsetComponent);

        return this.glue(new String[]{
            "SELECT", distinct, columns,
            "FROM", table,
            joins, wheres,
            groups, havings,
            orders, limit, offset});
    }

    protected String compileEmptyInsert(String table) {
        return this.glue(new String[]{
            "INSERT INTO", table, "() VALUES ()"
        });
    }

    protected String compileInsert(ComponentBag componentBag, Bindings bindings) {
        TableComponent tableComponent = componentBag.getFirst(TableComponent.class);
        List<ColumnComponent> columnComponents = componentBag.getAll(ColumnComponent.class);

        Object[] objects = bindings.get("values");

        String table = this.compileTable(tableComponent);
        String columns = this.compileInsertColumns(columnComponents);
        String values = "()";

        int columnLength = columns.isEmpty() ? 0 : this.split(columns).length;
        int rows = objects.length;

        if (0 == rows) {
            return this.compileEmptyInsert(table);
        }

        StringBuilder builder = new StringBuilder();

        if (0 < rows) {
            for (int i = 0; i < rows; i++) {
                Object[] row = Object[].class.cast(objects[i]);
                if (columnLength != row.length) {
                    throw new CompilerException("Value count not match column count at row " + (i + 1));
                }

                builder.append(",")
                        .append(this.getValueString(new Object[columnLength]));
            }
            values = builder.toString().replaceFirst(",", "");
        }

        return this.glue(new String[]{
            "INSERT INTO", table, "(" + columns + ")", "VALUES", values});
    }

    protected String compileUpdate(ComponentBag componentBag, Bindings bindings) {
        TableComponent tableComponent = componentBag.getFirst(TableComponent.class);
        List<ColumnComponent> columnComponents = componentBag.getAll(ColumnComponent.class);
        List<WhereComponent> whereComponents = componentBag.getAll(WhereComponent.class);

        if (columnComponents.isEmpty()) {
            throw new CompilerException("Undefined columns in update");
        }

        Object[] objects = bindings.get("values");

        if (0 == objects.length) {
            throw new CompilerException("Undefined values in update");
        }

        String table = this.compileTable(tableComponent);
        String columns = this.compileUpdateColumns(columnComponents);
        String wheres = this.compileWheres(whereComponents);

        int columnLength = this.split(columns).length;
        int valueLength = objects.length;

        if (columnLength != valueLength) {
            throw new CompilerException("Value count not match column count");
        }

        return this.glue(new String[]{"UPDATE", table, columns, wheres});
    }

    protected String compileDelete(ComponentBag componentBag) {
        TableComponent tableComponent = componentBag.getFirst(TableComponent.class);
        List<WhereComponent> whereComponents = componentBag.getAll(WhereComponent.class);

        String table = this.compileTable(tableComponent);
        String wheres = this.compileWheres(whereComponents);

        return this.glue(new String[]{"DELETE FROM", table, wheres});
    }

    @Override
    protected String compile(SQLType type, ComponentBag componentBag, Bindings bindings) {
        switch (type) {
            case SELECT:
                return this.compileSelect(componentBag);
            case INSERT:
                return this.compileInsert(componentBag, bindings);
            case UPDATE:
                return this.compileUpdate(componentBag, bindings);
            case DELETE:
                return this.compileDelete(componentBag);
            default:
                return null;
        }
    }
}
