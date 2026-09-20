# Using SQL Databases for TaskTide

The following is a guide for using SQL databases with TaskTide and outlined below. While provisioning RDBMS solutions is out of scope of TaskTide, it is suggested to follow standard [MariaDB Docker Instructions](https://hub.docker.com/_/mariadb). Similarly if an SQL backend is being used for TaskTide, then configuration of either [NoSQL backend](./NoSQL-Databases.md) or [ItemStore backend](./Embedded.md) is not required.

While maintenance is outside the scope of TaskTide, one resource for usable SQL database drivers is [JetBrains](https://www.jetbrains.com/datagrip/jdbc-drivers). Also note that while this resource aggregates a collection of drivers, that the they're accessible from developing institution.

Please Note ***it is not recommended to operate multiple database technologies with TaskTide***.

The following describes:
<ul>
    <li>A Database to point TaskTide to</li>
    <li>A JDBC implementation for TaskTide to interupt how to interact with RDBMS</li>
    <li>Applying configurations to the TaskTide config file</li>
</ul>

---

## 1). Provision an Ephemeral MariaDB Instance

Starts a MariaDB instance using latest docker image.

```bash
docker container run --rm \
    --name mariaDB \
    --detach \
    -e MARIADB_ROOT_PASSWORD=password \
    -e MARIADB_DATABASE=tasktide \
    -p 3306:3306 \
    mariadb:latest
```

---

## 2). Install MariaDB JDBC

The following instructions are relative to the root folder of the [release zip](https://github.com/BrenKenna/TaskTide/releases) which occur "tasktide-< VERSION >". Adjust upper path references according to your installation where appropriate.

Since the MySQL driver is provided within the TaskTide release zip, the following details using Microsoft SQL Server. A similar process can be used for other relational databases.

1. Download the required JDBC, if not known they are available from JetBrains at [this link](https://download.jetbrains.com/idea/jdbc-drivers/web/mssql-12.8.1.zip) which downloads version 12.8.1. Then place that jar file into the "Tasktide-< VERSION >/lib" folder.

2. Optionally remove the unused <i>tasktide-< VERSION >/lib/"mysql-connector-j-8.0.33.jar</i>".

3. Optionally, remove the unused JNoSQL JARs from "tasktide-< VERSION >/lib/".

4. Adjust Microsoft SQL Server [template config file](/tasktide/docs/configs/microsoft-sql-config.properties) according to your deployment.


---

## 3). Apply Ephemeral MariaDB Configs

Database configuration, listed below, is a ***global setting*** for TaskTide because each TaskTide library use it as their coordination layer. For instance the Manager-API provides the TaskTide repository and CRUD operations against it, the Engine-API uses it workload acquisition, and registering processing lifecycle events, and the Web-API exposes interactions against the Manager-API through a RESTful interface.

SQL support is provided through [JPA-Hibernate](https://www.baeldung.com/learn-jpa-hibernate) using [Hikari Data Source](https://www.baeldung.com/hikaricp) and by design their specific configurations can also be applied. Notice how the TaskTide specific configurations only look like labels/annotations (don't reference pool sizes etc), but that the core database configurations are external to TaskTide and refer to ***Hikari Connection Pool*** for database connection, and ***Hibernate*** for semantics.

A single [***Entity Manager***](https://jakarta.ee/specifications/persistence/2.2/apidocs/javax/persistence/entitymanager) per JVM/TaskTide instance provides "***Workflows***, ***Steps***, and ***WorkItems***" CRUD operations through TaskTideServiceManager-API.


| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Repository Type | Defines the database backend type to use| SQL | tasktide.core.repository.type | "<i><b>NA</b></i>" |
| Database URL | Defines the database to use for persisting "<i>Workflows, Steps, and WorkItems</i>" | jdbc:mysql://localhost:3306/tasktide_database | datasource.user | "<i><b>NA</b></i>" |
| Provider | Defines which database driver to use | com.mysql.cj.jdbc.Driver | datasource.driver | "<i><b>NA</b></i>" |
| Username | Username to use for authenticating requests | canBeSetAsAnEnvironmentalVariable | datasource.user | "<i><b>NA</b></i>" |
| Password | Password to use for authenticating user requests | canBeSetAsAnEnvironmentalVariable | datasource.password | "<i><b>NA</b></i>" |
| Dialect | SQL-JDBC bridge | org.hibernate.dialect.MariaDBDialect | hibernate.dialect | "<i><b>NA</b></i>" |
| DDL Auto | Schema generation tool see hibernate documentation <a href="https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch03.html#configuration-misc-properties">linked here</a> | update | hibernate.hbm2ddl.auto | "<i><b>NA</b></i>" |
