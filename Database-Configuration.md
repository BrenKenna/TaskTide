# Database Configuration Instructions

Since TaskTide supports SQL (ex Postgres, Maria etx), NoSQL () and embedded (RocksDB, SQLite) databases, separate instructions are provided for each. Loosely the steps involve fetching the appropriate driver, then configuring TaskTide to use the provisioned database with that driver. *Please not it is not necessary to install database drivers for ItemStore instances (RocksDB, SQLite) as these are provided with TaskTide*.

Separate guides for each TaskTide-Repository type have been provided and are linked below:

- **[Jakarta-NoSQL Database Instructions ➞](/docs/documentation/database-configuration/NoSQL-Databases.md)**

- **[Hibernate SQL Database Instructions ➞](/docs/documentation/database-configuration/SQL-Databases.md)**

- **[ItemStore Instructions ➞](/docs/documentation/database-configuration/Embedded-Databases.md)**