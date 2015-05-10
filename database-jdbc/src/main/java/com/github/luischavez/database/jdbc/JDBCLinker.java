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
package com.github.luischavez.database.jdbc;

import com.github.luischavez.database.configuration.DatabaseConfiguration;
import com.github.luischavez.database.link.Link;
import com.github.luischavez.database.link.Linker;
import com.github.luischavez.database.link.LinkerException;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public abstract class JDBCLinker implements Linker {

    private DataSource dataSource;

    protected <T extends DataSource> T getDataSource(Class<T> dataSourceClass) {
        return dataSourceClass.cast(this.dataSource);
    }

    protected Link createLink(Connection connection) {
        return new JDBCLink(connection);
    }

    protected abstract DataSource createDataSource(DatabaseConfiguration databaseConfiguration);

    @Override
    public void configure(DatabaseConfiguration databaseConfiguration) {
        this.dataSource = this.createDataSource(databaseConfiguration);
    }

    @Override
    public Link open() {
        Connection connection;
        try {
            connection = this.dataSource.getConnection();
        } catch (SQLException ex) {
            throw new LinkerException("Can't create link", ex);
        }
        return this.createLink(connection);
    }

    @Override
    public void close(Link link) {
        if (!(link instanceof JDBCLink)) {
            throw new LinkerException("Invalid Link class " + link.getClass().getName());
        }
        JDBCLink jdbcLink = JDBCLink.class.cast(link);
        try {
            jdbcLink.getConnection().close();
        } catch (SQLException ex) {
            throw new LinkerException("Can't close connection", ex);
        }
    }
}
