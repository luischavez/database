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

import com.github.luischavez.database.function.Fluentable;
import com.github.luischavez.database.function.Transform;
import com.github.luischavez.database.grammar.Bindings;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.nio.ByteBuffer;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.Map;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public abstract class Link {

    protected <T> void apply(Bundle bundle, Class<T> type, Transform<T> transform) {
        if (bundle.modified()) {
            return;
        }
        Object value = bundle.getValue();
        Class<? extends Object> clazz = value.getClass();
        if (type.isAssignableFrom(clazz)) {
            Object transformed = transform.apply(type.cast(value));
            bundle.setValue(transformed);
        }
    }

    protected Object transform(Object value, Fluentable<Bundle> fluentable) {
        if (null == value) {
            return value;
        }
        Bundle bundle = new Bundle(value);
        fluentable.fluent(bundle);
        if (!bundle.modified()) {
            throw new TransformException("Can't transform value " + value.getClass().getName() + " to java type");
        }
        return bundle.getValue();
    }

    protected Object toJava(Object value) {
        return this.transform(value, bundle -> {
            this.apply(bundle, Long.class, Transforms::same);
            this.apply(bundle, BigInteger.class, Transforms::toLong);
            this.apply(bundle, Integer.class, Transforms::toLong);
            this.apply(bundle, Short.class, Transforms::toLong);
            this.apply(bundle, BigDecimal.class, Transforms::same);
            this.apply(bundle, Float.class, Transforms::toBigDecimal);
            this.apply(bundle, Double.class, Transforms::toBigDecimal);
            this.apply(bundle, String.class, Transforms::same);
            this.apply(bundle, Clob.class, Transforms::toString);
            this.apply(bundle, NClob.class, Transforms::toString);
            this.apply(bundle, Character.class, Transforms::toString);
            this.apply(bundle, Date.class, Transforms::toLocalDate);
            this.apply(bundle, Time.class, Transforms::toLocalTime);
            this.apply(bundle, Timestamp.class, Transforms::toLocalDateTime);
            this.apply(bundle, Blob.class, Transforms::toByteBuffer);
            this.apply(bundle, byte[].class, Transforms::toByteBuffer);
            this.apply(bundle, Boolean.class, Transforms::same);
        });
    }

    protected Object toDatabase(Object value) {
        return this.transform(value, bundle -> {
            this.apply(bundle, Long.class, Transforms::same);
            this.apply(bundle, BigInteger.class, Transforms::toLong);
            this.apply(bundle, Integer.class, Transforms::toLong);
            this.apply(bundle, Short.class, Transforms::toLong);
            this.apply(bundle, BigDecimal.class, Transforms::same);
            this.apply(bundle, Float.class, Transforms::toBigDecimal);
            this.apply(bundle, Double.class, Transforms::toBigDecimal);
            this.apply(bundle, String.class, Transforms::same);
            this.apply(bundle, Clob.class, Transforms::toString);
            this.apply(bundle, NClob.class, Transforms::toString);
            this.apply(bundle, Character.class, Transforms::toString);
            this.apply(bundle, LocalDate.class, Transforms::toDate);
            this.apply(bundle, LocalTime.class, Transforms::toTime);
            this.apply(bundle, LocalDateTime.class, Transforms::toTimestamp);
            this.apply(bundle, ByteBuffer.class, Transforms::toBlob);
            this.apply(bundle, byte[].class, Transforms::toBlob);
            this.apply(bundle, Serializable.class, Transforms::toBlob);
            this.apply(bundle, Boolean.class, Transforms::same);
        });
    }

    protected void toDatabase(Bindings bindings) {
        if (null != bindings) {
            Object[] values = bindings.getArray(new String[]{"values"});
            for (int i = 0; i < values.length; i++) {
                values[i] = this.toDatabase(values[i]);
            }
            bindings.remove("values");
            bindings.set("values", values);
        }
    }

    protected Row toJava(Map<String, Object> values) {
        Row row = new Row();
        for (String key : values.keySet()) {
            row.set(key, this.toJava(values.get(key)));
        }
        return row;
    }

    public abstract RowList select(String sql, Bindings bindings);

    public abstract Affecting insert(String sql, Bindings bindings);

    public abstract Affecting update(String sql, Bindings bindings);

    public abstract Affecting delete(String sql, Bindings bindings);

    public abstract void create(String sql);

    public abstract void alter(String sql);

    public abstract void drop(String sql);

    public abstract boolean exists(String tableName);
}
