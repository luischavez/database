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

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class LinkerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LinkerException() {
    }

    public LinkerException(String message) {
        super(message);
    }

    public LinkerException(Throwable cause) {
        super(cause);
    }

    public LinkerException(String message, Throwable cause) {
        super(message, cause);
    }
}
