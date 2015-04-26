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

import com.github.luischavez.database.link.QueryException;
import com.github.luischavez.database.link.Transform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import java.math.BigDecimal;

import java.nio.ByteBuffer;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.stream.Collectors;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class JDBCTransform implements Transform {

    @Override
    public ByteBuffer getJavaBlob(Blob object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean getJavaBoolean(Object object) {
        return Boolean.class.cast(object);
    }

    @Override
    public LocalDate getJavaDate(Date object) {
        return object.toLocalDate();
    }

    @Override
    public LocalTime getJavaTime(Time object) {
        return object.toLocalTime();
    }

    @Override
    public LocalDateTime getJavaDateTime(Timestamp object) {
        return object.toLocalDateTime();
    }

    @Override
    public String getJavaString(Clob object) {
        String string = null;
        try (Reader reader = object.getCharacterStream();
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            string = bufferedReader.lines().collect(Collectors.joining());
        } catch (SQLException | IOException ex) {
            throw new QueryException("Can't get string value", ex);
        }
        return string;
    }

    @Override
    public Long getJavaLong(Object object) {
        return Long.class.cast(object);
    }

    @Override
    public Float getJavaFloat(Object object) {
        return Float.class.cast(object);
    }

    @Override
    public Double getJavaDouble(Object object) {
        return Double.class.cast(object);
    }

    @Override
    public BigDecimal getJavaDecimal(Object object) {
        return BigDecimal.class.cast(object);
    }

    @Override
    public Object getDatabaseBlob(ByteBuffer object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getDatabaseBoolean(Boolean object) {
        return object ? 1 : 0;
    }

    @Override
    public Object getDatabaseDate(LocalDate object) {
        return Date.valueOf(object);
    }

    @Override
    public Object getDatabaseTIme(LocalTime object) {
        return Time.valueOf(object);
    }

    @Override
    public Object getDatabaseDateTime(LocalDateTime object) {
        return Timestamp.valueOf(object);
    }

    @Override
    public Object getDatabaseString(String object) {
        return object;
    }

    @Override
    public Object getDatabaseLong(Long object) {
        return object;
    }

    @Override
    public Object getDatabaseFloat(Float object) {
        return object;
    }

    @Override
    public Object getDatabaseDouble(Double object) {
        return object;
    }

    @Override
    public Object getDatabaseDecimal(BigDecimal object) {
        return object;
    }
}
