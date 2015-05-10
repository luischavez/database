# Database access wrapper
[ ![Download](https://api.bintray.com/packages/luischavez/maven/database/images/download.svg) ](https://bintray.com/luischavez/maven/database/_latestVersion)
# Installation

# Requeriments
- Java >= 1.8

# Features
## Configuration
Configuration file example:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <databases>
        <database>
            <name>h2</name>
            <support>com.github.luischavez.database.h2.H2Support</support>
            <properties>
                <item key="database" value="test.h2"/>
                <item key="user" value="root"/>
                <item key="password" value="test"/>
            </properties>
        </database>
        <database>
            <name>mysql</name>
            <support>com.github.luischavez.database.mysql.MySQLSupport</support>
            <properties>
                <item key="server" value="localhost"/>
                <item key="database" value="test"/>
                <item key="port" value="3306"/>
                <item key="user" value="root"/>
                <item key="password" value=""/>
            </properties>
        </database>
    </databases>
    <migrators>
        <migrator>com.github.luischavez.database.examples.MyMigrator</migrator>
    </migrators>
</configuration>
```
### Local source
Local source configuration should be placed on the project working directory.
```java
Database.load(new XMLBuilder(), new LocalSource("database.xml"));
```
### Project source
Project source configuration (project resource) should be placed on project resources folder.
```java
Database.load(new XMLBuilder(), new ProjectSource("/database.xml"));
```
## Multi database support
```java
Database mysql = Database.use("mysql");
Database h2 = Database.use("h2");
```
## Query Builder
Before use database is necessary perform the connection.
```java
Database mysql = Database.use("mysql");
mysql.open();
```
And release the resources when finished.
```java
mysql.close();
```
### Query first result
```java
Row row = mysql.table("users").first();
```
### Query multiple results
```java
RowList rows = mysql.table("users").get();
```
### Select columns
```java
Row row = mysql.table("users").first("name, lastname");
RowList rows = mysql.table("users").get("name");
```
### Where clauses
```java
Row row = mysql.table("users").where("name", "=", "Luis").first();
```
Multiple where clauses
```java
Row row = mysql.table("users")
                .where("name", "=", "Luis")
                .orWhere("name", "=", "Walter")
                .first();
```
### Having clauses
```java
Row row = mysql.table("users").having("name", "=", "Luis").first();
```
Multiple having clauses
```java
Row row = mysql.table("users")
                .having("name", "=", "Luis")
                .orHaving("name", "=", "Walter")
                .first();
```
### Join clauses
```java
Row row = mysql.table("users u")
                .where("u.name", "=", "Luis")
                .join("profiles p", "p.user_id", "=", "u.user_id")
                .first();
```
Multiple join filters
```java
Row row = mysql.table("users u")
                .where("u.name", "=", "Luis")
                .join("profiles p", join -> {
                    join.on("p.user_id", "=", "u.user_id")
                        .or("p.user_id", "=", 100);
                })
                .first();
```
### Groups
```java
RowList rows = mysql.table("users")
                    .group("user_type")
                    .get("user_type, name, lastname");
```
### Ordering results
Asc order
```java
RowList rows = mysql.table("users")
                    .order("name", true);
                    .get("name");
```
Desc order
```java
RowList rows = mysql.table("users")
                    .order("name", false);
                    .get("name");
```
### Limit results
```java
RowList rows = mysql.table("users").limit(10).get();
```
Offset support
```java
RowList rows = mysql.table("users").limit(10).offset(5).get();
```
## Insert
Insert one row
```java
mysql.insert("users", "name, lastname", "Luis", "Chávez");
```
Insert multiple rows
```java
mysql.insert("users", "name, lastname", new Object[][] {
    {"Luis", "Chávez"},
    {"Walter", "White"}
});
```
Handle generated keys
```java
Affecting affecting = mysql.insert("users", "name, lastname", "Luis", "Chávez");
Object[] keys = affecting.getGeneratedKeys();
```
## Update
```java
mysql.where("name", "=", "Luis").update("users", "lastname", "Chávez");
```
## Delete
Delete all records
```java
mysql.delete("users");
```
Using filters
```java
mysql.where("name", "=", "Luis").delete("users");
```
## Schema builder
### Column types
Type        | Schema function
:---------- | :--------------
Boolean     | table.bool(columnName);
Time        | table.time(columnName);
Date        | table.date(columnName);
Datetime    | table.timestamp(columnName);
String      | table.string(columnName, length);
Text        | table.text(columnName);
Integer     | table.integer(columnName, length);
Decimal     | table.decimal(columnName, length, zeros);
### Definition
Type            | Schema function
:-------------- | :--------------
Null            | table.text(columnName).nullable();
Unsigned        | table.integer(columnName).unsigned();
Autoincrement   | table.integer(columnName).incremented();
Default         | table.timestamp(columnName).defaults(value);
### Constraint types
Type        | Schema function
:---------- | :--------------
Primary     | table.primary(columnName);
Unique      | table.unique(columnName);
Index       | table.index(columnName);
Foreign     | table.foreign(columnName, relatedTableName, relatedColumnName, onDelete, onUpdate);
### Create
```java
mysql.create("users", table -> {
    table.integer("user_id").incremented();
    table.string("name", 64);
    table.string("lastname", 64);
    table.string("user_type", 20).defaults("USER");
    table.primary("user_id");
});
```
### Alter
```java
mysql.table("users", table -> {
    // add columns.
    table.timestamp("register_datetime");
    // modify columns.
    table.modify(columns -> {
        columns.string("user_type", 10).defaults("ADMIN");
    });
    // drop columns.
    table.drop("user_type");
    // drop primary key.
    table.dropPrimary();
});
```
### Drop
```java
mysql.drop("users");
```
### Exists
```java
if (mysql.exists("users")) {
}
```
## Migrations
Define migrations
```java
public class CreateUsersTable implements Migration {
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
```
Create custom migrator
```java
public class MyMigrator extends Migrator {
    @Override
    public void setup() {
        this.register(new CreateUsersTable());
    }
}
```
Configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <migrators>
        <migrator>com.github.luischavez.database.examples.MyMigrator</migrator>
    </migrators>
</configuration>
```
### Migrate
```java
mysql.migrate();
```
### Rollback
```java
mysql.rollback();
```
# Authors
- Luis Chávez <https://github.com/luischavez>