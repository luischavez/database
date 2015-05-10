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
package com.github.luischavez.database;

import com.github.luischavez.database.link.Row;
import com.github.luischavez.database.link.RowList;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public abstract class Migrator {

    private final List<Migration> migrations;

    public Migrator() {
        this.migrations = new ArrayList<>();
    }

    protected void register(Migration migration) {
        this.migrations.add(migration);
    }

    protected void createMigrationTable(Database database) {
        database.create("migrations", table -> {
            table.text("migration");
            table.timestamp("created_at");
        });
    }

    protected Migration createInstace(String migrationClassName) {
        Class<?> migrationClass;
        try {
            migrationClass = Class.forName(migrationClassName);
        } catch (ClassNotFoundException ex) {
            throw new MigrationException("Can't resolve " + migrationClassName, ex);
        }
        if (!Migration.class.isAssignableFrom(migrationClass)) {
            throw new MigrationException("Invalid migration " + migrationClassName);
        }
        Migration migration;
        try {
            migration = Migration.class.cast(migrationClass.newInstance());
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new MigrationException("Can't create instance of " + migrationClassName, ex);
        }
        return migration;
    }

    public void migrate(Database database) {
        if (!database.exists("migrations")) {
            this.createMigrationTable(database);
        }
        for (Migration migration : this.migrations) {
            Row row = database.table("migrations").where("migration", "=", migration.getClass().getName()).first();
            if (null == row) {
                migration.up(database);
                database.insert("migrations", "migration, created_at", migration.getClass().getName(), LocalDateTime.now());
            }
        }
    }

    public void rollback(Database database) {
        if (!database.exists("migrations")) {
            return;
        }
        RowList rows = database.table("migrations").order("created_at", false).get();
        for (Row row : rows) {
            Object migrationClassName = row.value("migration");
            Migration migration = this.createInstace(migrationClassName.toString());
            migration.down(database);
            database.where("migration", "=", migrationClassName).delete("migrations");
        }
    }

    public abstract void setup();
}
