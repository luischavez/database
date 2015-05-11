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
package com.github.luischavez.database.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
@XmlRootElement(name = "configuration")
public class Configuration {

    private final List<DatabaseConfiguration> databases;
    private final List<String> migrators;

    public Configuration() {
        this.databases = new ArrayList<>();
        this.migrators = new ArrayList<>();
    }

    public List<DatabaseConfiguration> getDatabases() {
        return databases;
    }

    @XmlElementWrapper(name = "databases")
    @XmlElement(name = "database")
    public void setDatabases(List<DatabaseConfiguration> databaseConfigurations) {
        this.databases.clear();
        this.databases.addAll(databaseConfigurations);
    }

    public List<String> getMigrators() {
        return migrators;
    }

    @XmlElementWrapper(name = "migrators")
    @XmlElement(name = "migrator")
    public void setMigrators(List<String> migrators) {
        this.migrators.clear();
        this.migrators.addAll(migrators);
    }
}
