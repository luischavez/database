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
package com.github.luischavez.database;

import com.github.luischavez.database.grammar.Grammar;
import com.github.luischavez.database.link.Linker;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public abstract class Support {

    private final Linker linker;
    private final Grammar queryGrammar;
    private final Grammar schemaGrammar;

    public Support(Linker linker, Grammar queryGrammar, Grammar schemaGrammar) {
        this.linker = linker;
        this.queryGrammar = queryGrammar;
        this.schemaGrammar = schemaGrammar;
    }

    public Linker linker() {
        return this.linker;
    }

    public Grammar queryGrammar() {
        return this.queryGrammar;
    }

    public Grammar schemaGrammar() {
        return this.schemaGrammar;
    }
}
