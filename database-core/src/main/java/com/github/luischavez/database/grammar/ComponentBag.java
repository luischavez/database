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
import java.util.List;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class ComponentBag {

    private final List<Component> components;

    public ComponentBag() {
        this.components = new ArrayList<>();
    }

    public <T extends Component> T getFirst(Class<T> componentClass) {
        T firstComponent = null;
        for (Component component : this.components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                firstComponent = componentClass.cast(component);
                break;
            }
        }
        return firstComponent;
    }

    public <T extends Component> List<T> getAll(Class<T> componentClass) {
        List<T> filteredComponents = new ArrayList<>();
        for (Component component : this.components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                filteredComponents.add(componentClass.cast(component));
            }
        }
        return filteredComponents;
    }

    public <T extends Component> void add(T component) {
        this.components.add(component);
    }

    public <T extends Component> boolean removeFirst(Class<T> componentClass) {
        T firstComponent = this.getFirst(componentClass);
        if (null == firstComponent) {
            return false;
        }
        return this.components.remove(firstComponent);
    }

    public <T extends Component> boolean removeAll(Class<T> componentClass) {
        List<T> allComponents = this.getAll(componentClass);
        if (allComponents.isEmpty()) {
            return false;
        }
        return this.components.removeAll(allComponents);
    }

    public <T extends Component> boolean contains(Class<T> componentClass) {
        for (Component component : this.components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                return true;
            }
        }
        return false;
    }
}
