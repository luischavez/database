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
# Authors
- Luis Chávez <https://github.com/luischavez>