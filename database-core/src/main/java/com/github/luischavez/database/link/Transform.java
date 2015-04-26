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
package com.github.luischavez.database.link;

import java.math.BigDecimal;

import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public interface Transform {

    public ByteBuffer getJavaBlob(Blob object);

    public Boolean getJavaBoolean(Object object);

    public LocalDate getJavaDate(Date object);

    public LocalTime getJavaTime(Time object);

    public LocalDateTime getJavaDateTime(Timestamp object);

    public String getJavaString(Clob object);

    public Long getJavaLong(Object object);

    public Float getJavaFloat(Object object);

    public Double getJavaDouble(Object object);

    public BigDecimal getJavaDecimal(Object object);

    public Object getDatabaseBlob(ByteBuffer object);

    public Object getDatabaseBoolean(Boolean object);

    public Object getDatabaseDate(LocalDate object);

    public Object getDatabaseTIme(LocalTime object);

    public Object getDatabaseDateTime(LocalDateTime object);

    public Object getDatabaseString(String object);

    public Object getDatabaseLong(Long object);

    public Object getDatabaseFloat(Float object);

    public Object getDatabaseDouble(Double object);

    public Object getDatabaseDecimal(BigDecimal object);
}
