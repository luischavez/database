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

import com.github.luischavez.database.function.Fluentable;
import com.github.luischavez.database.grammar.Bindings;
import com.github.luischavez.database.grammar.ColumnType;
import com.github.luischavez.database.grammar.Compilable;
import com.github.luischavez.database.grammar.ComponentBag;
import com.github.luischavez.database.grammar.ConstraintType;
import com.github.luischavez.database.grammar.SQLType;
import com.github.luischavez.database.schema.component.ColumnDefinition;
import com.github.luischavez.database.schema.component.ConstraintDefinition;
import com.github.luischavez.database.schema.component.CreateColumnComponent;
import com.github.luischavez.database.schema.component.CreateConstraintComponent;
import com.github.luischavez.database.schema.component.DropColumnComponent;
import com.github.luischavez.database.schema.component.DropConstraintComponent;
import com.github.luischavez.database.schema.component.ForeignDefinition;
import com.github.luischavez.database.schema.component.SchemaTableComponent;

import java.math.BigDecimal;

import java.nio.ByteBuffer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class Blueprint implements Compilable {

    private final SQLType type;
    private final ComponentBag componentBag;
    private final Bindings bindings;

    public Blueprint(SQLType type, String tableName) {
        this.type = type;
        this.componentBag = new ComponentBag();
        this.bindings = new Bindings();
        this.componentBag.add(new SchemaTableComponent(tableName));
    }

    protected <T extends Object> Schema<T> createColumn(String columnName, ColumnType columnType, int length, int zeros) {
        Schema<T> schema = new Schema<>(columnType);
        ColumnDefinition definition = schema.getDefinition();
        definition.setLength(length);
        definition.setZeros(zeros);
        this.componentBag.add(new CreateColumnComponent(columnName, definition));
        return schema;
    }

    protected <T extends Object> Schema<T> createColumn(String columnName, ColumnType columnType, int length) {
        return this.createColumn(columnName, columnType, length, 0);
    }

    protected <T extends Object> Schema<T> createColumn(String columnName, ColumnType columnType) {
        return this.createColumn(columnName, columnType, 0);
    }

    public Schema<Integer> integer(String columnName, int length) {
        return this.createColumn(columnName, ColumnType.INTEGER, length);
    }

    public Schema<Integer> integer(String columnName) {
        return this.integer(columnName, 0);
    }

    public Schema<BigDecimal> decimal(String columnName, int length, int zeros) {
        return this.createColumn(columnName, ColumnType.DECIMAL, length, zeros);
    }

    public Schema<String> string(String columnName, int length) {
        return this.createColumn(columnName, ColumnType.STRING, length);
    }

    public Schema<String> text(String columnName) {
        return this.createColumn(columnName, ColumnType.TEXT);
    }

    public Schema<LocalDate> date(String columnName) {
        return this.createColumn(columnName, ColumnType.DATE);
    }

    public Schema<LocalTime> time(String columnName) {
        return this.createColumn(columnName, ColumnType.TIME);
    }

    public Schema<LocalDateTime> dateTime(String columnName) {
        return this.createColumn(columnName, ColumnType.DATE_TIME);
    }

    public Schema<ByteBuffer> binary(String columnName) {
        return this.createColumn(columnName, ColumnType.BINARY);
    }

    public Schema<Boolean> bool(String columnName) {
        return this.createColumn(columnName, ColumnType.BOOLEAN);
    }

    public void modify(Fluentable<Blueprint> fluentable) {
        Blueprint blueprint = new Blueprint(SQLType.ALTER, null);
        fluentable.fluent(blueprint);
        ComponentBag components = blueprint.components();
        List<CreateColumnComponent> createColumnComponents = components.getAll(CreateColumnComponent.class);
        for (CreateColumnComponent createColumnComponent : createColumnComponents) {
            createColumnComponent.setAlter(true);
            this.componentBag.add(createColumnComponent);
        }
    }

    public void drop(String columnName) {
        DropColumnComponent dropColumnComponent = new DropColumnComponent(columnName);
        this.componentBag.add(dropColumnComponent);
    }

    protected void createConstraint(String columnName, ConstraintType constratinType, String constraintName) {
        ConstraintDefinition definition = new ConstraintDefinition(constratinType);
        String fixedConstraintName = constraintName.replaceAll(" ", "").replaceAll(",", "_");
        definition.setConstraintName(fixedConstraintName);
        CreateConstraintComponent createConstraintComponent = new CreateConstraintComponent(columnName, definition);
        this.componentBag.add(createConstraintComponent);
    }

    public void primary(String columnName, String constraintName) {
        this.createConstraint(columnName, ConstraintType.PRIMARY_KEY, constraintName);
    }

    public void primary(String columnName) {
        this.primary(columnName, columnName.toLowerCase().concat("_pk"));
    }

    public void unique(String columnName, String constraintName) {
        this.createConstraint(columnName, ConstraintType.UNIQUE, constraintName);
    }

    public void unique(String columnName) {
        this.unique(columnName, columnName.toLowerCase().concat("_uq"));
    }

    public void index(String columnName, String constraintName) {
        this.createConstraint(columnName, ConstraintType.INDEX, constraintName);
    }

    public void index(String columnName) {
        this.index(columnName, columnName.toLowerCase().concat("_ix"));
    }

    public void foreign(String columnName, String constraintName, String relatedColumnName, String relatedTableName, String onDelete, String onUpdate) {
        ForeignDefinition definition = new ForeignDefinition(ConstraintType.FOREIGN_KEY, relatedColumnName, relatedTableName, onDelete, onUpdate);
        definition.setConstraintName(constraintName);
        CreateConstraintComponent createConstraintComponent = new CreateConstraintComponent(columnName, definition);
        this.componentBag.add(createConstraintComponent);
    }

    public void foreign(String columnName, String relatedTableName, String relatedColumnName, String onDelete, String onUpdate) {
        SchemaTableComponent schemaTableComponent = this.componentBag.getFirst(SchemaTableComponent.class);
        String tableName = schemaTableComponent.getTableName();
        String constraintName = tableName.concat("_").concat(relatedTableName).concat("_fk");
        this.foreign(columnName, constraintName, relatedColumnName, relatedTableName, onDelete, onUpdate);
    }

    protected void dropConstraint(ConstraintType constraintType, String constraintName) {
        ConstraintDefinition definition = new ConstraintDefinition(constraintType);
        definition.setConstraintName(constraintName);
        DropConstraintComponent dropConstraintComponent = new DropConstraintComponent(definition);
        this.componentBag.add(dropConstraintComponent);
    }

    public void dropPrimary() {
        this.dropConstraint(ConstraintType.PRIMARY_KEY, "");
    }

    public void dropUnique(String constraintName) {
        this.dropConstraint(ConstraintType.UNIQUE, constraintName);
    }

    public void dropIndex(String constraintName) {
        this.dropConstraint(ConstraintType.INDEX, constraintName);
    }

    public void dropForeign(String constraintName) {
        this.dropConstraint(ConstraintType.FOREIGN_KEY, constraintName);
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
