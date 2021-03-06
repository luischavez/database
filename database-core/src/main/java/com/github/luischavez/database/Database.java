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
package com.github.luischavez.database;

import com.github.luischavez.database.configuration.Configuration;
import com.github.luischavez.database.configuration.ConfigurationBuilder;
import com.github.luischavez.database.configuration.ConfigurationSource;
import com.github.luischavez.database.configuration.DatabaseConfiguration;
import com.github.luischavez.database.function.Fluentable;
import com.github.luischavez.database.grammar.Compiler;
import com.github.luischavez.database.grammar.Grammar;
import com.github.luischavez.database.grammar.SQLType;
import com.github.luischavez.database.handler.DefaultHandler;
import com.github.luischavez.database.handler.Handler;
import com.github.luischavez.database.link.Affecting;
import com.github.luischavez.database.link.Link;
import com.github.luischavez.database.link.Row;
import com.github.luischavez.database.link.RowList;
import com.github.luischavez.database.query.Query;
import com.github.luischavez.database.query.Queryable;
import com.github.luischavez.database.query.component.JoinComponent;
import com.github.luischavez.database.schema.Blueprint;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class Database implements Queryable<Query> {

    private static final List<DatabaseConfiguration> CONFIGURATIONS = new ArrayList<>();
    private static final List<Migrator> MIGRATORS = new ArrayList<>();

    private final DatabaseConfiguration databaseConfiguration;
    private final Support support;

    private Link link;

    public Database(DatabaseConfiguration databaseConfiguration, Support support) {
        this.databaseConfiguration = databaseConfiguration;
        this.support = support;
        this.link = null;
    }

    public void configure() {
        this.support.linker().configure(this.databaseConfiguration);
    }

    public void open() {
        if (null != this.link) {
            this.support.linker().close(this.link);
        }
        this.link = this.support.linker().open();
    }

    public void close() {
        if (null != this.link) {
            this.support.linker().close(this.link);
            this.link = null;
        }
    }

    protected Handler handle(Compiler compiler) {
        if (null == this.link) {
            throw new DatabaseException("Connection to database isn't open");
        }
        return new DefaultHandler(compiler, this.link);
    }

    protected void schema(Blueprint blueprint) {
        Grammar grammar = this.support.schemaGrammar();
        Handler handler = this.handle(grammar);
        handler.execute(blueprint);
    }

    public void migrate() {
        for (Migrator migrator : Database.MIGRATORS) {
            migrator.migrate(this);
        }
    }

    public void rollback() {
        for (Migrator migrator : Database.MIGRATORS) {
            migrator.rollback(this);
        }
    }

    public void reset() {
        for (Migrator migrator : Database.MIGRATORS) {
            migrator.reset(this);
        }
    }

    public Query query() {
        Grammar grammar = this.support.queryGrammar();
        Handler handler = this.handle(grammar);
        return new Query(handler);
    }

    public void create(String tableName, Fluentable<Blueprint> fluentable) {
        Blueprint blueprint = new Blueprint(SQLType.CREATE, tableName);
        fluentable.fluent(blueprint);
        this.schema(blueprint);
    }

    public void table(String tableName, Fluentable<Blueprint> fluentable) {
        Blueprint blueprint = new Blueprint(SQLType.ALTER, tableName);
        fluentable.fluent(blueprint);
        this.schema(blueprint);
    }

    public boolean exists(String tableName) {
        return this.link.exists(tableName);
    }

    public void drop(String tableName) {
        Blueprint blueprint = new Blueprint(SQLType.DROP, tableName);
        this.schema(blueprint);
    }

    @Override
    public Query table(String tableName) {
        return this.query().table(tableName);
    }

    @Override
    public Query distinct(boolean onlyDistinctResults) {
        return this.query().distinct(onlyDistinctResults);
    }

    @Override
    public Query join(String tableName, String firstColumn, String operator, String secondColumn) {
        return this.query().join(tableName, firstColumn, operator, secondColumn);
    }

    @Override
    public Query join(String tableName, Fluentable<JoinComponent> fluentable) {
        return this.query().join(tableName, fluentable);
    }

    @Override
    public Query naturalJoin(String tableName, String firstColumn, String operator, String secondColumn) {
        return this.query().naturalJoin(tableName, firstColumn, operator, secondColumn);
    }

    @Override
    public Query naturalJoin(String tableName, Fluentable<JoinComponent> fluentable) {
        return this.query().naturalJoin(tableName, fluentable);
    }

    @Override
    public Query leftJoin(String tableName, String firstColumn, String operator, String secondColumn) {
        return this.query().leftJoin(tableName, firstColumn, operator, secondColumn);
    }

    @Override
    public Query leftJoin(String tableName, Fluentable<JoinComponent> fluentable) {
        return this.query().leftJoin(tableName, fluentable);
    }

    @Override
    public Query rightJoin(String tableName, String firstColumn, String operator, String secondColumn) {
        return this.query().rightJoin(tableName, firstColumn, operator, secondColumn);
    }

    @Override
    public Query rightJoin(String tableName, Fluentable<JoinComponent> fluentable) {
        return this.query().rightJoin(tableName, fluentable);
    }

    @Override
    public Query fullJoin(String tableName, String firstColumn, String operator, String secondColumn) {
        return this.query().fullJoin(tableName, firstColumn, operator, secondColumn);
    }

    @Override
    public Query fullJoin(String tableName, Fluentable<JoinComponent> fluentable) {
        return this.query().fullJoin(tableName, fluentable);
    }

    @Override
    public Query where(String columnName, String operator, Object value) {
        return this.query().where(columnName, operator, value);
    }

    @Override
    public Query orWhere(String columnName, String operator, Object value) {
        return this.query().orWhere(columnName, operator, value);
    }

    @Override
    public Query group(String columnName) {
        return this.query().group(columnName);
    }

    @Override
    public Query having(String columnName, String operator, Object value) {
        return this.query().having(columnName, operator, value);
    }

    @Override
    public Query orHaving(String columnName, String operator, Object value) {
        return this.query().orHaving(columnName, operator, value);
    }

    @Override
    public Query order(String columnName, boolean ascendant) {
        return this.query().order(columnName, ascendant);
    }

    @Override
    public Query limit(int maxResults) {
        return this.query().limit(maxResults);
    }

    @Override
    public Query offset(int firstResultIndex) {
        return this.query().offset(firstResultIndex);
    }

    @Override
    public Affecting insert(String tableName, String columns, Object... values) {
        return this.query().insert(tableName, columns, new Object[][]{values});
    }

    @Override
    public Affecting insert(String tableName, String columns, Object[][] values) {
        return this.query().insert(tableName, columns, values);
    }

    @Override
    public Affecting update(String tableName, String columns, Object... values) {
        return this.query().update(tableName, columns, values);
    }

    @Override
    public Affecting delete(String tableName) {
        return this.query().delete(tableName);
    }

    @Override
    public RowList get(String... columns) {
        throw new UnsupportedOperationException("Use table method before fetch results");
    }

    @Override
    public Row first(String... columns) {
        throw new UnsupportedOperationException("Use table method before fetch results");
    }

    private static <T> T createInstance(Class<T> type, String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new InvalidSupportClassException("Can't find class " + className, ex);
        }
        if (!type.isAssignableFrom(clazz)) {
            throw new InvalidSupportClassException("Class " + className + " not implements " + type.getName());
        }
        Object object;
        try {
            object = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new InvalidSupportClassException("Can't load class " + className, ex);
        }
        return type.cast(object);
    }

    private static Database createDatabase(DatabaseConfiguration configuration) {
        Support support = Database.createInstance(Support.class, configuration.getSupportClassName());
        Database database = new Database(configuration, support);
        database.configure();
        return database;
    }

    public static Database use(String name) {
        for (DatabaseConfiguration configuration : Database.CONFIGURATIONS) {
            if (configuration.getName().equals(name)) {
                return Database.createDatabase(configuration);
            }
        }
        throw new DatabaseException("Database not found " + name);
    }

    private static void loadDatabases(Configuration configuration) {
        Database.CONFIGURATIONS.clear();
        Database.CONFIGURATIONS.addAll(configuration.getDatabases());
    }

    private static void loadMigrators(Configuration configuration) {
        Database.MIGRATORS.clear();
        List<String> migrators = configuration.getMigrators();
        for (String migratorClassName : migrators) {
            Migrator migrator = Database.createInstance(Migrator.class, migratorClassName);
            migrator.setup();
            Database.MIGRATORS.add(migrator);
        }
    }

    public static void load(ConfigurationBuilder builder, ConfigurationSource source) {
        Configuration configuration = builder.build(source);
        Database.loadDatabases(configuration);
        Database.loadMigrators(configuration);
    }
}
