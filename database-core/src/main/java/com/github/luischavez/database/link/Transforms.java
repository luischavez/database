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
package com.github.luischavez.database.link;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.nio.ByteBuffer;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class Transforms {

    public static Object same(Object value) {
        return value;
    }

    public static Long toLong(BigInteger value) {
        return value.longValue();
    }

    public static Long toLong(Integer value) {
        return value.longValue();
    }

    public static Long toLong(Short value) {
        return value.longValue();
    }

    public static BigDecimal toBigDecimal(Float value) {
        return new BigDecimal(value);
    }

    public static BigDecimal toBigDecimal(Double value) {
        return new BigDecimal(value);
    }

    public static String toString(Clob value) {
        String string = null;
        try (Reader reader = value.getCharacterStream();
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            string = bufferedReader.lines().collect(Collectors.joining());
        } catch (SQLException | IOException ex) {
            throw new TransformException("Can't transform " + value.getClass().getName() + " to String", ex);
        }
        return string;
    }

    public static String toString(NClob value) {
        String string = null;
        try (Reader reader = value.getCharacterStream();
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            string = bufferedReader.lines().collect(Collectors.joining());
        } catch (SQLException | IOException ex) {
            throw new TransformException("Can't transform " + value.getClass().getName() + " to String", ex);
        }
        return string;
    }

    public static String toString(Character value) {
        return value.toString();
    }

    public static LocalDate toLocalDate(Date value) {
        return value.toLocalDate();
    }

    public static Date toDate(LocalDate value) {
        return Date.valueOf(value);
    }

    public static LocalTime toLocalTime(Time value) {
        return value.toLocalTime();
    }

    public static Time toTime(LocalTime value) {
        return Time.valueOf(value);
    }

    public static LocalDateTime toLocalDateTime(Timestamp value) {
        return value.toLocalDateTime();
    }

    public static Timestamp toTimestamp(LocalDateTime value) {
        return Timestamp.valueOf(value);
    }

    public static ByteBuffer toByteBuffer(Blob value) {
        byte[] bytes;
        try {
            bytes = value.getBytes(0, (int) value.length());
        } catch (SQLException ex) {
            throw new TransformException("Can't transform " + value.getClass().getName() + " to ByteBuffer", ex);
        }
        return ByteBuffer.wrap(bytes);
    }

    public static ByteBuffer toByteBuffer(byte[] value) {
        return ByteBuffer.wrap(value);
    }

    public static Blob toBlob(ByteBuffer value) {
        Blob blob;
        try {
            blob = new SerialBlob(value.array());
        } catch (SQLException ex) {
            throw new TransformException("Can't transform " + value.getClass().getName() + " to Blob", ex);
        }
        return blob;
    }

    public static Blob toBlob(byte[] value) {
        Blob blob;
        try {
            blob = new SerialBlob(value);
        } catch (SQLException ex) {
            throw new TransformException("Can't transform " + value.getClass().getName() + " to Blob", ex);
        }
        return blob;
    }

    public static Blob toBlob(Serializable value) {
        Blob blob;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(baos)) {
            outputStream.writeObject(value);
            blob = new SerialBlob(baos.toByteArray());
        } catch (SQLException | IOException ex) {
            throw new TransformException("Can't transform " + value.getClass().getName() + " to Blob", ex);
        }
        return blob;
    }
}
