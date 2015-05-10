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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class Row {

    private final Map<String, Object> valueMap;

    public Row() {
        this.valueMap = new HashMap<>();
    }

    public void fill(Map<String, Object> valueMap) {
        this.valueMap.putAll(valueMap);
    }

    public void set(String key, Object value) {
        this.valueMap.put(key, value);
    }

    public Object value(String key) {
        return this.valueMap.get(key);
    }

    public <T extends Object> T value(String key, Class<T> type) {
        Object value = this.value(key);
        return null == value ? null : type.cast(value);
    }

    public String[] keys() {
        return this.valueMap.keySet().toArray(new String[0]);
    }

    public void clear() {
        this.valueMap.clear();
    }
}
