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

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class LocalSource implements ConfigurationSource {

    private final String filePath;

    public LocalSource(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public byte[] read() {
        String userDir = System.getProperty("user.dir");
        File file = new File(userDir + File.separator + this.filePath);
        if (!file.exists()) {
            throw new ConfigurationSourceException("File " + file.getPath() + " not exist");
        }
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            throw new ConfigurationSourceException("Can't read file " + file.getPath(), ex);
        }
        return bytes;
    }
}
