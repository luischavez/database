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

import com.github.luischavez.database.grammar.Bindings;
import com.github.luischavez.database.link.Affecting;
import com.github.luischavez.database.link.Link;
import com.github.luischavez.database.link.QueryException;
import com.github.luischavez.database.link.RowList;
import com.github.luischavez.database.link.Row;
import com.github.luischavez.database.link.Transform;

import java.math.BigDecimal;

import java.nio.ByteBuffer;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class JDBCLink implements Link {

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

    protected RowList buildListResults(ResultSet resultSet, Transform transform) {
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
                Row result = new Row();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i + 1);
                    Object columnValue = resultSet.getObject(i + 1);
                    Object javaObject = columnValue;
                    if (null != transform) {
                        javaObject = this.toJavaObject(columnValue, transform);
                    }
                    result.set(columnName.toLowerCase(), javaObject);
                }
                listResults.attach(result);
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

    protected Affecting execute(String sql, Bindings bindings, Transform transform, boolean generateKeys) {
        PreparedStatement statement = this.preparedStatement(sql, generateKeys);
        if (null != bindings) {
            Object[] values = bindings.getArray(new String[]{"values"});
            if (null != transform) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = this.toDatabaseObject(values[i], transform);
                }
            }
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
        }
        this.close(resultSet);
        this.close(statement);
        return new Affecting(affectingCount, keys);
    }

    @Override
    public RowList select(String sql, Bindings bindings, Transform transform) {
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
        RowList result = this.buildListResults(resultSet, transform);
        this.close(resultSet);
        this.close(statement);
        return result;
    }

    @Override
    public Affecting insert(String sql, Bindings bindings, Transform transform) {
        return this.execute(sql, bindings, transform, true);
    }

    @Override
    public Affecting update(String sql, Bindings bindings, Transform transform) {
        return this.execute(sql, bindings, transform, false);
    }

    @Override
    public Affecting delete(String sql, Bindings bindings) {
        return this.execute(sql, bindings, null, false);
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

    protected Object toDatabaseObject(Object object, Transform transform) {
        Class<? extends Object> objectClass = object.getClass();
        if (ByteBuffer.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseBlob(ByteBuffer.class.cast(object));
        }
        if (Boolean.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseBoolean(Boolean.class.cast(object));
        }
        if (LocalDate.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseDate(LocalDate.class.cast(object));
        }
        if (LocalTime.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseTIme(LocalTime.class.cast(object));
        }
        if (LocalDateTime.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseDateTime(LocalDateTime.class.cast(object));
        }
        if (String.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseString(String.class.cast(object));
        }
        if (Integer.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseLong(Integer.class.cast(object).longValue());
        }
        if (Long.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseLong(Long.class.cast(object));
        }
        if (Float.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseFloat(Float.class.cast(object));
        }
        if (Double.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseDouble(Double.class.cast(object));
        }
        if (BigDecimal.class.isAssignableFrom(objectClass)) {
            return transform.getDatabaseDecimal(BigDecimal.class.cast(object));
        }
        return object;
    }

    protected Object toJavaObject(Object object, Transform transform) {
        Class<? extends Object> objectClass = object.getClass();
        if (Clob.class.isAssignableFrom(objectClass)) {
            return transform.getJavaString(Clob.class.cast(object));
        }
        if (Blob.class.isAssignableFrom(objectClass)) {
            return transform.getJavaBlob(Blob.class.cast(object));
        }
        if (Date.class.isAssignableFrom(objectClass)) {
            return transform.getJavaDate(Date.class.cast(object));
        }
        if (Time.class.isAssignableFrom(objectClass)) {
            return transform.getJavaTime(Time.class.cast(object));
        }
        if (Timestamp.class.isAssignableFrom(objectClass)) {
            return transform.getJavaDateTime(Timestamp.class.cast(object));
        }
        if (Integer.class.isAssignableFrom(objectClass)) {
            return transform.getJavaLong(Integer.class.cast(object).longValue());
        }
        return object;
    }
}
