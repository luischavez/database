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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class RowList implements Iterable<Row> {

    private final List<Row> rows;

    public RowList() {
        this.rows = new LinkedList<>();
    }

    public void attach(Row row) {
        this.rows.add(row);
    }

    public void detach(Row row) {
        if (this.rows.contains(row)) {
            this.rows.remove(row);
        }
    }

    public Row getRow(int index) {
        return this.rows.get(index);
    }

    public int size() {
        return this.rows.size();
    }

    public boolean empty() {
        return this.rows.isEmpty();
    }

    @Override
    public Iterator<Row> iterator() {
        return new ResultIterator();
    }

    class ResultIterator implements Iterator<Row> {

        int currentIndex = 0;

        @Override
        public boolean hasNext() {
            int size = RowList.this.size();
            return this.currentIndex < size;
        }

        @Override
        public Row next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return RowList.this.getRow(this.currentIndex++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
