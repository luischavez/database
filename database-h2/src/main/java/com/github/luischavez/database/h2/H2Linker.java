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
package com.github.luischavez.database.h2;

import com.github.luischavez.database.configuration.Configuration;
import com.github.luischavez.database.configuration.ConfigurationException;
import com.github.luischavez.database.jdbc.JDBCLinker;

import java.util.Map;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class H2Linker extends JDBCLinker {

    @Override
    protected DataSource createDataSource(Configuration configuration) {
        Map<String, String> properties = configuration.getProperties();
        if (!properties.containsKey("database")) {
            throw new ConfigurationException("Undefined database property");
        }
        if (!properties.containsKey("user")) {
            throw new ConfigurationException("Undefined user property");
        }
        if (!properties.containsKey("password")) {
            throw new ConfigurationException("Undefined password property");
        }
        String database = properties.get("database");
        String user = properties.get("user");
        String password = properties.get("password");
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
        }
        JdbcDataSource h2DataSource = new JdbcDataSource();
        String userDir = System.getProperty("user.dir");
        h2DataSource.setURL("jdbc:h2:file:" + userDir + "/" + database);
        h2DataSource.setUser(user);
        h2DataSource.setPassword(password);
        return h2DataSource;
    }
}
