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
package com.github.luischavez.database.configuration;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class Configuration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String supportClassName;

    private final Map<String, String> properties;

    public Configuration(String name, String supportClassName, Map<String, String> properties) {
        this.name = name;
        this.supportClassName = supportClassName;
        this.properties = new HashMap<>(properties);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupportClassName() {
        return this.supportClassName;
    }

    public void setSupportClassName(String supportClassName) {
        this.supportClassName = supportClassName;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }
}
