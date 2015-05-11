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
package com.github.luischavez.database.handler;

import com.github.luischavez.database.grammar.Bindings;
import com.github.luischavez.database.grammar.Compilable;
import com.github.luischavez.database.grammar.Compiler;
import com.github.luischavez.database.grammar.SQLType;
import com.github.luischavez.database.link.Affecting;
import com.github.luischavez.database.link.Link;
import com.github.luischavez.database.link.RowList;
import com.github.luischavez.database.link.Transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class DefaultHandler implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHandler.class);

    private final Compiler compiler;
    private final Link link;
    private final Transform transform;

    public DefaultHandler(Compiler compiler, Link link, Transform transform) {
        this.compiler = compiler;
        this.link = link;
        this.transform = transform;
    }

    protected void log(String sql, Bindings bindings) {
        StringBuilder builder = new StringBuilder();
        if (null != bindings) {
            Object[] objects = bindings.getArray();
            builder.append("{");
            for (int i = 0; i < objects.length; i++) {
                builder.append(String.format("%n\t"))
                        .append(i + 1).append(" => ").append(objects[i])
                        .append(",");
            }
            builder.append(String.format("%n}"));
        }
        String formatedBindings = builder.toString();
        LOGGER.debug("Executing: {}, Bindings: {}", sql, formatedBindings);
    }

    @Override
    public RowList fetch(Compilable compilable) {
        if (!SQLType.SELECT.equals(compilable.type())) {
            throw new InvalidSQLException("Only SELECT statements can fetch results");
        }
        String sql = this.compiler.compile(compilable);
        Bindings bindings = compilable.bindings();
        RowList rows = this.link.select(sql, bindings, this.transform);
        this.log(sql, bindings);
        return rows;
    }

    @Override
    public Affecting affect(Compilable compilable) {
        String sql = this.compiler.compile(compilable);
        Bindings bindings = compilable.bindings();
        SQLType type = compilable.type();
        Affecting affecting = null;
        switch (type) {
            case INSERT:
                affecting = this.link.insert(sql, bindings, this.transform);
                break;
            case UPDATE:
                affecting = this.link.update(sql, bindings, this.transform);
                break;
            case DELETE:
                affecting = this.link.delete(sql, bindings);
                break;
            default:
                throw new InvalidSQLException("Invalid DML type " + type);
        }
        this.log(sql, bindings);
        return affecting;
    }

    @Override
    public void execute(Compilable compilable) {
        String sql = this.compiler.compile(compilable);
        SQLType type = compilable.type();
        switch (type) {
            case CREATE:
                this.link.create(sql);
                break;
            case ALTER:
                this.link.alter(sql);
                break;
            case DROP:
                this.link.drop(sql);
                break;
            default:
                throw new InvalidSQLException("Invalid DDL type: " + type);
        }
        this.log(sql, null);
    }
}
