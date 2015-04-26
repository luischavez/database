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
package com.github.luischavez.database.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class ProjectSource implements ConfigurationSource {

    private final String resourceName;

    public ProjectSource(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public byte[] read() {
        URL url = getClass().getResource(this.resourceName);

        byte[] bytes;
        try (InputStream inputStream = url.openStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = inputStream.read(buffer)) != -1;) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.flush();

            bytes = outputStream.toByteArray();
        } catch (IOException ex) {
            throw new ConfigurationSourceException("Can't read resource " + url.getPath(), ex);
        }

        return bytes;
    }
}
