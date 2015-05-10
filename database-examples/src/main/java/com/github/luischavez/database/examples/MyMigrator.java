/*
 * Copyright (C) 2015 Luis Chávez {@literal <https://github.com/luischavez>}
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
package com.github.luischavez.database.examples;

import com.github.luischavez.database.Database;
import com.github.luischavez.database.Migration;
import com.github.luischavez.database.Migrator;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class MyMigrator extends Migrator {

    @Override
    public void setup() {
        this.register(new CreateUserTable());
    }

    public static class CreateUserTable implements Migration {

        @Override
        public void up(Database database) {
            database.create("users", table -> {
                table.string("name", 32);
                table.string("lastname", 32);
            });
        }

        @Override
        public void down(Database database) {
            database.drop("users");
        }
    }
}
