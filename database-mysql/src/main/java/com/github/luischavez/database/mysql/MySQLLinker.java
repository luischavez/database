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
package com.github.luischavez.database.mysql;

import com.github.luischavez.database.configuration.Configuration;
import com.github.luischavez.database.configuration.ConfigurationException;
import com.github.luischavez.database.jdbc.JDBCLinker;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.util.Map;

import javax.sql.DataSource;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class MySQLLinker extends JDBCLinker {

    @Override
    public DataSource createDataSource(Configuration configuration) {
        Map<String, String> properties = configuration.getProperties();
        if (!properties.containsKey("server")) {
            throw new ConfigurationException("Undefined server property");
        }
        if (!properties.containsKey("database")) {
            throw new ConfigurationException("Undefined database property");
        }
        if (!properties.containsKey("user")) {
            throw new ConfigurationException("Undefined user property");
        }
        String server = properties.get("server");
        String database = properties.get("database");
        String user = properties.get("user");
        String password = properties.get("password");
        MysqlConnectionPoolDataSource mysqlDataSource = new MysqlConnectionPoolDataSource();
        mysqlDataSource.setServerName(server);
        mysqlDataSource.setDatabaseName(database);
        mysqlDataSource.setUser(user);
        mysqlDataSource.setPassword(password);
        return mysqlDataSource;
    }
}
