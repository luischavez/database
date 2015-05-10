/*
 * Copyright (C) 2015 Luis Chávez {@literal <https://github.com/luischavez>}
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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
@XmlRootElement(name = "database")
public class DatabaseConfiguration {

    private String name;
    private String supportClassName;

    private final Map<String, String> properties;

    public DatabaseConfiguration() {
        this.properties = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getSupportClassName() {
        return this.supportClassName;
    }

    @XmlElement(name = "support")
    public void setSupportClassName(String supportClassName) {
        this.supportClassName = supportClassName;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    @XmlJavaTypeAdapter(XMLMapAdapter.class)
    public void setProperties(Map<String, String> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }
}