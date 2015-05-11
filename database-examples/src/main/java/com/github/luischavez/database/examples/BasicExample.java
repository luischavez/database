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
import com.github.luischavez.database.link.Affecting;
import com.github.luischavez.database.link.RowList;
import com.github.luischavez.database.link.Row;

import java.time.LocalDateTime;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class BasicExample implements Example {

    private static final String USER_TABLE_NAME = "users";
    private static final String PROFILE_TABLE_NAME = "profiles";

    protected void createTables(Database database) {
        database.create(USER_TABLE_NAME, table -> {
            table.integer("user_id").incremented();
            table.string("username", 32);
            table.string("pass", 32);

            table.primary("user_id");
            table.unique("pass");
        });
        database.create(PROFILE_TABLE_NAME, table -> {
            table.integer("profile_id").incremented();
            table.integer("user_id");
            table.text("description").nullable();

            table.primary("profile_id");
            table.foreign("user_id", "users", "user_id", "CASCADE", "CASCADE");
        });
    }

    protected void alterTables(Database database) {
        database.table(USER_TABLE_NAME, table -> {
            table.drop("pass");
            table.string("password", 32).defaults("test");
            table.timestamp("register_date").defaults(LocalDateTime.now());

            table.modify(columns -> columns.string("username", 50).defaults("modified"));

            table.dropUnique("pass_uq");
            table.unique("username");
        });
    }

    protected void dropTables(Database database) {
        database.drop(PROFILE_TABLE_NAME);
        database.drop(USER_TABLE_NAME);
    }

    @Override
    public void execute(Database database) {
        this.createTables(database);
        this.alterTables(database);

        Affecting insert = database.insert(USER_TABLE_NAME, "username, password, register_date",
                "luischavez", "test", LocalDateTime.now());

        Object[] keys = insert.getGeneratedKeys();

        for (Object key : keys) {
            database.where("user_id", "=", key)
                    .update("users", "password", "encrypted");

            database.insert(PROFILE_TABLE_NAME, "user_id, description", key, "lorem ipsum etc etc");
        }

        RowList users = database.table("users u")
                .join("profiles p", "u.user_id", "=", "p.user_id")
                .get();
        this.log("User count: {}", users.size());
        for (Row user : users) {
            this.log("Username: {}, Password: {}, Register: {}, Description: {}",
                    user.value("username"), user.value("password"), user.value("register_date"), user.value("description"));
        }

        Row user = database.table(USER_TABLE_NAME).first();
        this.log("First user: {}", user.value("username"));

        database.where("user_id", "IN", keys).delete(USER_TABLE_NAME);

        this.dropTables(database);
    }
}
