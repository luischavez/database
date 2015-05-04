# Database access wrapper

# Installation

# Requeriments
- Java >= 1.8

# Features
## Configuration
Configuration file example:
```xml
<configurations>
    <configuration>
        <name>h2</name>
        <support>com.github.luischavez.database.h2.H2Support</support>
        <properties>
            <database>database.h2</database>
            <user>root</user>
            <password>test</password>
        </properties>
    </configuration>
    <configuration>
        <name>mysql</name>
        <support>com.github.luischavez.database.mysql.MySQLSupport</support>
        <properties>
            <server>localhost</server>
            <database>example</database>
            <port>3306</port>
            <user>root</user>
            <password></password>
        </properties>
    </configuration>
</configurations>
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
Row row = mysql.query("users").first();
```
### Query multiple results
```java
RowList rows = mysql.query("users").get();
```
### Select columns
```java
Row row = mysql.query("users").first("name, lastname");
RowList rows = mysql.query("users").get("name");
```
### Where clauses
```java
Row row = mysql.query("users").where("name", "=", "Luis").first();
```
Multiple where clauses
```java
Row row = mysql.query("users")
                .where("name", "=", "Luis")
                .orWhere("name", "=", "Walter")
                .first();
```
### Having clauses
```java
Row row = mysql.query("users").having("name", "=", "Luis").first();
```
Multiple having clauses
```java
Row row = mysql.query("users")
                .having("name", "=", "Luis")
                .orHaving("name", "=", "Walter")
                .first();
```
### Join clauses
```java
Row row = mysql.query("users u")
                .where("u.name", "=", "Luis")
                .join("profiles p", "p.user_id", "=", "u.user_id")
                .first();
```
Multiple join filters
```java
Row row = mysql.query("users u")
                .where("u.name", "=", "Luis")
                .join("profiles p", join -> {
                    join.on("p.user_id", "=", "u.user_id")
                        .or("p.user_id", "=", 100);
                })
                .first();
```
### Groups
```java
RowList rows = mysql.query("users")
                    .group("user_type")
                    .get("user_type, name, lastname");
```
### Ordering results
Asc order
```java
RowList rows = mysql.query("users")
                    .order("name", true);
                    .get("name");
```
Desc order
```java
RowList rows = mysql.query("users")
                    .order("name", false);
                    .get("name");
```
### Limit results
```java
RowList rows = mysql.query("users").limit(10).get();
```
Offset support
```java
RowList rows = mysql.query("users").limit(10).offset(5).get();
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
# Authors
- Luis Chávez <https://github.com/luischavez>