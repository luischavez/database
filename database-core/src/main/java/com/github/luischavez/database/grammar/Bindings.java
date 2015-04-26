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
package com.github.luischavez.database.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class Bindings {

    private final Map<String, List<Object>> bindingMap;

    public Bindings() {
        this.bindingMap = new HashMap<>();
    }

    public boolean has(String type) {
        return this.bindingMap.containsKey(type);
    }

    public Object[] get(String type) {
        if (!this.bindingMap.containsKey(type)) {
            return new Object[0];
        }

        List<Object> objects = this.bindingMap.get(type);

        return objects.toArray();
    }

    public void remove(String type) {
        this.bindingMap.remove(type);
    }

    public void set(String type, Object object) {
        if (!this.bindingMap.containsKey(type)) {
            this.bindingMap.put(type, new ArrayList<>());
        }

        List<Object> objects = this.bindingMap.get(type);

        if ("values".equals(type) && !(object instanceof Object[])) {
            objects.add(new Object[]{object});
        } else {
            objects.add(object);
        }
    }

    public Object[] getArray(String[] types) {
        List<Object> allObjects = new ArrayList<>();

        for (String type : types) {
            Object[] objects = this.get(type);

            for (Object object : objects) {
                if (object instanceof Object[]) {
                    Object[] subObjects = Object[].class.cast(object);
                    allObjects.addAll(Arrays.asList(subObjects));
                } else {
                    allObjects.add(object);
                }
            }
        }

        return allObjects.toArray();
    }

    public Object[] getArray() {
        Set<String> keys = this.bindingMap.keySet();

        String[] types = keys.toArray(new String[0]);
        return this.getArray(types);
    }
}
