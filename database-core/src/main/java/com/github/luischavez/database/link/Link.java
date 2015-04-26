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
package com.github.luischavez.database.link;

import com.github.luischavez.database.grammar.Bindings;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public interface Link {

    public RowList select(String sql, Bindings bindings, Transform transform);

    public Affecting insert(String sql, Bindings bindings, Transform transform);

    public Affecting update(String sql, Bindings bindings, Transform transform);

    public Affecting delete(String sql, Bindings bindings);

    public void create(String sql);

    public void alter(String sql);

    public void drop(String sql);
}
