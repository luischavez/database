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
package com.github.luischavez.database.grammar;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public abstract class Grammar implements Compiler {

    protected String wrap(String string) {
        return String.format("%s", string);
    }

    protected String glue(String union, String[] strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            if (null != string && !string.isEmpty()) {
                builder.append(union).append(string);
            }
        }
        return builder.toString().replaceFirst(union, "").trim();
    }

    protected String glue(String[] strings) {
        return this.glue(" ", strings);
    }

    protected abstract String compile(SQLType type, ComponentBag componentBag, Bindings bindings);

    @Override
    public String compile(Compilable compilable) {
        SQLType type = compilable.type();
        ComponentBag componentBag = compilable.components();
        Bindings bindings = compilable.bindings();
        String compiled = this.compile(type, componentBag, bindings);
        if (null == compiled) {
            throw new CompilerException("Unsupported SQL " + type.name());
        }
        return compiled;
    }
}
