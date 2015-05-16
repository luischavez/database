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
package com.github.luischavez.database.examples;

import com.github.luischavez.database.Database;
import com.github.luischavez.database.link.Row;

import java.io.Serializable;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class SerializeExample implements Example {

    protected void schema(Database database) {
        if (database.exists("serial")) {
            database.drop("serial");
        }
        database.create("serial", table -> {
            table.binary("bin");
        });
    }

    @Override
    public void execute(Database database) {
        this.schema(database);
        if (database.insert("serial", "bin", new Person("Luis")).success()) {
            Row serial = database.table("serial").first("bin");
            Person person = serial.object("bin", Person.class);
            System.out.println(person.name);
        }
    }

    public static class Person implements Serializable {

        private static final long serialVersionUID = 1L;

        public String name;

        public Person(String name) {
            this.name = name;
        }
    }
}
