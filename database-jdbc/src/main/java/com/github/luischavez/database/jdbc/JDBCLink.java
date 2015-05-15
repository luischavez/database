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
package com.github.luischavez.database.jdbc;

import com.github.luischavez.database.grammar.Bindings;
import com.github.luischavez.database.link.Affecting;
import com.github.luischavez.database.link.Link;
import com.github.luischavez.database.link.QueryException;
import com.github.luischavez.database.link.RowList;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class JDBCLink extends Link {

    private final Connection connection;

    public JDBCLink(Connection connection) {
        this.connection = connection;
    }

    protected Connection getConnection() {
        return this.connection;
    }

    protected PreparedStatement preparedStatement(String sql, boolean generateKeys) {
        PreparedStatement statement;
        try {
            if (generateKeys) {
                statement = this.connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            } else {
                statement = this.connection.prepareStatement(sql);
            }
        } catch (SQLException ex) {
            throw new QueryException("Can't prepare statement", ex);
        }
        return statement;
    }

    protected Statement createStatement() {
        Statement statement;
        try {
            statement = this.connection.createStatement();
        } catch (SQLException ex) {
            throw new QueryException("Can't prepare statement", ex);
        }
        return statement;
    }

    protected void close(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ex) {
            throw new QueryException("Can't close resource", ex);
        }
    }

    protected void setBindings(PreparedStatement statement, Object[] objects, int firstIndex) {
        for (int i = 0; i < objects.length; i++) {
            try {
                statement.setObject(firstIndex + i, objects[i]);
            } catch (SQLException ex) {
                throw new QueryException("Invalid parameter " + objects[i] + " at index " + i, ex);
            }
        }
    }

    protected RowList buildListResults(ResultSet resultSet) {
        RowList listResults = new RowList();
        ResultSetMetaData metaData;
        try {
            metaData = resultSet.getMetaData();
        } catch (SQLException ex) {
            throw new QueryException("Can't get meta data", ex);
        }
        try {
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                HashMap<String, Object> values = new HashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i + 1);
                    Object columnValue = resultSet.getObject(i + 1);
                    values.put(columnName.toLowerCase(), columnValue);
                }
                listResults.attach(this.toJava(values));
            }
        } catch (SQLException ex) {
            throw new QueryException("Can't create result", ex);
        }
        return listResults;
    }

    protected Object[] getGeneratedKeys(ResultSet resultSet) {
        List<Object> keys = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Object keyValue = resultSet.getObject(1);
                keys.add(keyValue);
            }
        } catch (SQLException ex) {
            throw new QueryException("Can't get generated keys", ex);
        }
        return keys.toArray();
    }

    protected void executeDDL(String sql) {
        Statement statement = this.createStatement();
        try {
            String[] parts = sql.split(";");
            for (String part : parts) {
                statement.executeUpdate(part);
            }
        } catch (SQLException ex) {
            throw new QueryException("Can't execute ddl", ex);
        }
        this.close(statement);
    }

    protected Affecting execute(String sql, Bindings bindings, boolean generateKeys) {
        PreparedStatement statement = this.preparedStatement(sql, generateKeys);
        if (null != bindings) {
            this.toDatabase(bindings);
            Object[] values = bindings.getArray(new String[]{"values"});
            Object[] wheres = bindings.getArray(new String[]{"wheres"});
            this.setBindings(statement, values, 1);
            this.setBindings(statement, wheres, values.length + 1);
        }
        int affectingCount;
        ResultSet resultSet = null;
        try {
            affectingCount = statement.executeUpdate();
            if (generateKeys) {
                resultSet = statement.getGeneratedKeys();
            }
        } catch (SQLException ex) {
            throw new QueryException("Can't execute sql", ex);
        }
        Object[] keys = new Object[0];
        if (null != resultSet) {
            keys = this.getGeneratedKeys(resultSet);
            this.close(resultSet);
        }
        this.close(statement);
        return new Affecting(affectingCount, keys);
    }

    @Override
    public RowList select(String sql, Bindings bindings) {
        PreparedStatement statement = this.preparedStatement(sql, false);
        if (null != bindings) {
            Object[] wheresAndHavings = bindings.getArray(new String[]{"wheres", "havings"});
            this.setBindings(statement, wheresAndHavings, 1);
        }
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery();
        } catch (SQLException ex) {
            throw new QueryException("Can't execute query", ex);
        }
        RowList result = this.buildListResults(resultSet);
        this.close(resultSet);
        this.close(statement);
        return result;
    }

    @Override
    public Affecting insert(String sql, Bindings bindings) {
        return this.execute(sql, bindings, true);
    }

    @Override
    public Affecting update(String sql, Bindings bindings) {
        return this.execute(sql, bindings, false);
    }

    @Override
    public Affecting delete(String sql, Bindings bindings) {
        return this.execute(sql, bindings, false);
    }

    @Override
    public void create(String sql) {
        this.executeDDL(sql);
    }

    @Override
    public void alter(String sql) {
        this.executeDDL(sql);
    }

    @Override
    public void drop(String sql) {
        this.executeDDL(sql);
    }

    @Override
    public boolean exists(String tableName) {
        boolean exists = false;
        try {
            DatabaseMetaData databaseMetaData = this.connection.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, null, tableName.toUpperCase(), null);
            exists = tables.next();
            this.close(tables);
        } catch (SQLException ex) {
            throw new QueryException("Can't get database metadata", ex);
        }
        return exists;
    }
}
