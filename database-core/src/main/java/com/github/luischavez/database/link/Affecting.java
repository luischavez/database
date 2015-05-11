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
package com.github.luischavez.database.link;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class Affecting {

    private final int affectingCount;
    private final Object[] generatedKeys;

    public Affecting(int affectingCount, Object[] generatedkeys) {
        this.affectingCount = affectingCount;
        this.generatedKeys = generatedkeys;
    }

    public boolean success() {
        return 0 < this.affectingCount;
    }

    public boolean fails() {
        return 0 == this.affectingCount;
    }

    public Object[] getGeneratedKeys() {
        return this.generatedKeys;
    }

    public int count() {
        return this.affectingCount;
    }
}
