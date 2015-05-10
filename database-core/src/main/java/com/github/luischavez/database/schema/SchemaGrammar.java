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
package com.github.luischavez.database.schema;

import com.github.luischavez.database.grammar.Bindings;
import com.github.luischavez.database.grammar.CompilerException;
import com.github.luischavez.database.grammar.ComponentBag;
import com.github.luischavez.database.grammar.Grammar;
import com.github.luischavez.database.grammar.SQLType;
import com.github.luischavez.database.schema.component.ColumnDefinition;
import com.github.luischavez.database.schema.component.ConstraintDefinition;
import com.github.luischavez.database.schema.component.CreateColumnComponent;
import com.github.luischavez.database.schema.component.CreateConstraintComponent;
import com.github.luischavez.database.schema.component.DropColumnComponent;
import com.github.luischavez.database.schema.component.DropConstraintComponent;
import com.github.luischavez.database.schema.component.ForeignDefinition;
import com.github.luischavez.database.schema.component.SchemaTableComponent;

import java.util.List;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class SchemaGrammar extends Grammar {

    protected String getColumnTypeString(String columnType) {
        return columnType;
    }

    protected String getConstraintTypeString(String constraintType) {
        return constraintType;
    }

    protected String getLengthString(int length, int zeros) {
        if (0 == length) {
            return "";
        }
        if (0 == zeros) {
            return "(" + length + ")";
        }
        return "(" + length + "," + zeros + ")";
    }

    protected String getUnsignedString(boolean unsigned) {
        return unsigned ? "UNSIGNED" : "";
    }

    protected String getAutoIncrementString(boolean incremented) {
        return incremented ? "AUTO_INCREMENT" : "";
    }

    protected String getNullableString(boolean nullable) {
        return nullable ? "NULL" : "NOT NULL";
    }

    protected String getDefaultValueString(Object defaultValue) {
        if (null == defaultValue) {
            return "";
        }
        if (defaultValue instanceof Boolean) {
            Boolean bool = Boolean.class.cast(defaultValue);
            return "DEFAULT '" + (bool ? "1" : "0") + "'";
        }
        return "DEFAULT '" + defaultValue.toString() + "'";
    }

    protected String getColumnString(String columnName, ColumnDefinition definition) {
        return this.glue(new String[]{
            this.wrap(columnName),
            this.getColumnTypeString(definition.getColumnType()),
            this.getLengthString(definition.getLength(), definition.getZeros()),
            this.getUnsignedString(definition.isUnsigned()),
            this.getNullableString(definition.isNullable()),
            this.getAutoIncrementString(definition.isIncremented()),
            this.getDefaultValueString(definition.getDefaultValue())
        });
    }

    protected String getAddColumnString(String columnName, ColumnDefinition definition) {
        String column = this.getColumnString(columnName, definition);
        return this.glue(new String[]{
            "ADD", column
        });
    }

    protected String getAlterColumnString(String columnName, ColumnDefinition definition) {
        String column = this.getColumnString(columnName, definition);
        return this.glue(new String[]{
            "MODIFY COLUMN", column
        });
    }

    protected String getDropColumnString(String columnName) {
        return "DROP COLUMN ".concat(this.wrap(columnName));
    }

    protected String getOnDeleteString(String onDelete) {
        return "ON DELETE ".concat(onDelete);
    }

    protected String getOnUpdateString(String onUpdate) {
        return "ON UPDATE ".concat(onUpdate);
    }

    protected String getConstraintString(String columnName, ConstraintDefinition definition) {
        if (definition instanceof ForeignDefinition) {
            ForeignDefinition foreign = ForeignDefinition.class.cast(definition);
            return this.glue(new String[]{
                "CONSTRAINT",
                this.wrap(definition.getConstraintName()),
                this.getConstraintTypeString(definition.getConstraintType()),
                "(", this.wrap(columnName), ")",
                "REFERENCES", this.wrap(foreign.getRelatedTableName()),
                "(" + this.wrap(foreign.getRelatedColumnName()) + ")",
                this.getOnDeleteString(foreign.getOnDelete()),
                this.getOnUpdateString(foreign.getOnUpdate())
            });
        }
        return this.glue(new String[]{
            "CONSTRAINT",
            this.wrap(definition.getConstraintName()),
            this.getConstraintTypeString(definition.getConstraintType()),
            "(", this.wrap(columnName), ")"
        });
    }

    protected String getAddConstraintString(String columnName, ConstraintDefinition definition) {
        return this.glue(new String[]{
            "ADD", this.getConstraintString(columnName, definition)
        });
    }

    protected String getDropConstraintString(ConstraintDefinition definition) {
        return this.glue(new String[]{
            "DROP",
            this.getConstraintTypeString(definition.getConstraintType()),
            this.wrap(definition.getConstraintName())
        });
    }

    protected String compileTable(SchemaTableComponent schemaTableComponent) {
        if (null == schemaTableComponent) {
            throw new CompilerException("Undefined table");
        }
        String tableName = schemaTableComponent.getTableName();
        return this.wrap(tableName);
    }

    protected String compileColumns(List<CreateColumnComponent> createColumnComponents) {
        StringBuilder builder = new StringBuilder();
        for (CreateColumnComponent createColumnComponent : createColumnComponents) {
            String columnName = createColumnComponent.getColumnName();
            ColumnDefinition definition = createColumnComponent.getDefinition();
            String column = this.getColumnString(columnName, definition);
            builder.append(",").append(column);
        }
        return builder.toString().replaceFirst(",", "");
    }

    protected String compileAlterColumns(String table, List<CreateColumnComponent> createColumnComponents) {
        StringBuilder builder = new StringBuilder();
        for (CreateColumnComponent createColumnComponent : createColumnComponents) {
            String columnName = createColumnComponent.getColumnName();
            ColumnDefinition definition = createColumnComponent.getDefinition();
            String alter = this.glue(new String[]{
                "ALTER TABLE", table,
                (createColumnComponent.isAlter()
                ? this.getAlterColumnString(columnName, definition)
                : this.getAddColumnString(columnName, definition)),
                ";"
            });
            builder.append(alter);
        }
        return builder.toString();
    }

    protected String compileDropColumns(String table, List<DropColumnComponent> dropColumnComponents) {
        StringBuilder builder = new StringBuilder();
        for (DropColumnComponent dropColumnComponent : dropColumnComponents) {
            String columnName = dropColumnComponent.getColumnName();
            String drop = this.glue(new String[]{
                "ALTER TABLE", table,
                this.getDropColumnString(columnName),
                ";"
            });
            builder.append(drop);
        }
        return builder.toString();
    }

    protected String compileConstraints(List<CreateConstraintComponent> createConstraintComponents) {
        StringBuilder builder = new StringBuilder();
        for (CreateConstraintComponent createConstraintComponent : createConstraintComponents) {
            String columnName = createConstraintComponent.getColumnName();
            ConstraintDefinition definition = createConstraintComponent.getDefinition();
            String constraint = this.getConstraintString(columnName, definition);
            builder.append(",").append(constraint);
        }
        return builder.toString().replaceFirst(",", "");
    }

    protected String compileAlterConstraints(String table, List<CreateConstraintComponent> createConstraintComponents) {
        StringBuilder builder = new StringBuilder();
        for (CreateConstraintComponent createConstraintComponent : createConstraintComponents) {
            String columnName = createConstraintComponent.getColumnName();
            ConstraintDefinition definition = createConstraintComponent.getDefinition();
            String alter = this.glue(new String[]{
                "ALTER TABLE", table,
                this.getAddConstraintString(columnName, definition),
                ";"
            });
            builder.append(alter);
        }
        return builder.toString();
    }

    protected String compileDropConstraints(String table, List<DropConstraintComponent> dropConstraintComponents) {
        StringBuilder builder = new StringBuilder();
        for (DropConstraintComponent dropConstraintComponent : dropConstraintComponents) {
            ConstraintDefinition definition = dropConstraintComponent.getDefinition();
            String drop = this.glue(new String[]{
                "ALTER TABLE", table,
                this.getDropConstraintString(definition),
                ";"
            });
            builder.append(drop);
        }
        return builder.toString();
    }

    protected String compileCreate(ComponentBag componentBag) {
        SchemaTableComponent schemaTableComponent = componentBag.getFirst(SchemaTableComponent.class);
        List<CreateColumnComponent> createColumnComponents = componentBag.getAll(CreateColumnComponent.class);
        List<CreateConstraintComponent> createConstraintComponents = componentBag.getAll(CreateConstraintComponent.class);
        String table = this.compileTable(schemaTableComponent);
        String columns = this.compileColumns(createColumnComponents);
        String constraints = this.compileConstraints(createConstraintComponents);
        return this.glue(new String[]{
            "CREATE TABLE", table,
            "(",
            this.glue(",", new String[]{
                columns, constraints
            }),
            ");"
        });
    }

    protected String compileAlter(ComponentBag componentBag) {
        SchemaTableComponent schemaTableComponent = componentBag.getFirst(SchemaTableComponent.class);
        List<DropConstraintComponent> dropConstraintComponents = componentBag.getAll(DropConstraintComponent.class);
        List<CreateConstraintComponent> createConstraintComponents = componentBag.getAll(CreateConstraintComponent.class);
        List<DropColumnComponent> dropColumnComponents = componentBag.getAll(DropColumnComponent.class);
        List<CreateColumnComponent> createColumnComponents = componentBag.getAll(CreateColumnComponent.class);
        String table = this.compileTable(schemaTableComponent);
        String dropConstraints = this.compileDropConstraints(table, dropConstraintComponents);
        String alterConstraints = this.compileAlterConstraints(table, createConstraintComponents);
        String dropColumns = this.compileDropColumns(table, dropColumnComponents);
        String alterColumns = this.compileAlterColumns(table, createColumnComponents);
        return this.glue(new String[]{
            dropConstraints,
            dropColumns,
            alterColumns,
            alterConstraints
        });
    }

    protected String compileDrop(ComponentBag componentBag) {
        SchemaTableComponent schemaTableComponent = componentBag.getFirst(SchemaTableComponent.class);
        String table = this.compileTable(schemaTableComponent);
        return this.glue(new String[]{
            "DROP TABLE", table, ";"
        });
    }

    @Override
    protected String compile(SQLType type, ComponentBag componentBag, Bindings bindings) {
        switch (type) {
            case CREATE:
                return this.compileCreate(componentBag);
            case ALTER:
                return this.compileAlter(componentBag);
            case DROP:
                return this.compileDrop(componentBag);
            default:
                return null;
        }
    }
}
