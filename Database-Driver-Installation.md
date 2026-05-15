# Database Driver Installation
<p>
The following relates to NoSQL & Relational Databases only, meaning this can be ignored if either RocksDB/SQLite are being used (both included in TaskTide <a href="https://github.com/BrenKenna/TaskTide/releases">release zips</a>). The JDBC drivers for databases like Postgres, MySQL, MariaDB etc can downloaded from preferred source or from the collection maintained by JetBrains <a  href="https://www.jetbrains.com/datagrip/jdbc-drivers/#">linked here</a>, and an example installaion is <a href="/Database-Driver-Installation.md#1-install-required-relational-database-driver">provided here</a>.
</p>

<p>
A collection of Jakarta-NoSQL database drivers have been included within TaskTide. These are MongoDB and CouchDB for DocumentTemplate, Cassandra for ColumnTemplate, Redis and DynamoDB for KeyValueTemplate with their use is <a href="/Database-Driver-Installation.md#2-using-pre-packaged-nosql-database-driver">described here</a>. In the event that other database drivers are required see the <a href="/Database-Driver-Installation.md#3-using-another-nosql-database-driver">following guide</a>.
</p>


## 1). Install Required Relational Database Driver
<p>
The following instructions are relative to the root folder of the <a href="https://github.com/BrenKenna/TaskTide/releases">release zip</a> which occur "tasktide-< VERSION >". Adjust upper path references according to your installation where appropriate. Since the MySQL driver is provided within the TaskTide release zip, the following details using Microsoft SQL Server. A similar process can be used for other relational databases.
</p>

<p>
 1. Download the required JDBC, if not known they are available from JetBrains at <a href="https://download.jetbrains.com/idea/jdbc-drivers/web/mssql-12.8.1.zip">this link</a> which downloads version 12.8.1. Then place that jar file into the "Tasktide-< VERSION >/lib" folder.
 2. Optionally remove the unused <i>tasktide-< VERSION >/lib/"mysql-connector-j-8.0.33.jar</i>".
 3. Optionally, remove the unused JNoSQL JARs from "tasktide-< VERSION >/lib/".
 4. Adjust Microsoft SQL Server <a href="/tasktide/docs/configs/microsoft-sql-config.properties">template config file</a> according to your deployment.
 </p>

## 2). Using Pre-Packaged NoSQL-Database Driver
<p>
The pre-packaged NoSQL databases are couchDB, MongoDB, ArangoDB, couchBase, DynamoDB, Cassandra and Redis. The instructions below can adapted for <a href="https://github.com/eclipse-jnosql/jnosql-databases">preferred database</a>.
</p>

<p>
1. Optionally, remove the JNoSQL JARs. Since couchDB uses the <a href="https://github.com/eclipse-jnosql/jnosql-databases?tab=readme-ov-file#couchdb">DocumentTemplate</a>. This would be the Graph, KeyValue and Column jars Communication-Column/Key-Value/Graph.jar, and Mapping JNoSQL JARs.
2. Then optionally delete the unused JNoSQL Cassandra, ArangoDB, CouchBase, DynamoDB, MongoDB, and JNoSQL JARs.
</p>

## 3). Using Another NoSQL-Database Driver
<p>
An example few NoSQL databases were packaged with TaskTide for unit testing purposes. These are couchDB, MongoDB, ArangoDB, couchBase, DynamoDB, Cassandra and Redis. It is important to note that TaskTide was designed for ETL scale-outs which supported < THESE RESEARCH PAPERS >, where <a href="https://github.com/BrenKenna/pyanamo">couchDB/DynamoDB</a> were used. Since these databases all performed really well. The choice in the backend is considered "<i>dealers choice</i>"/what is more conveniently deployed, because TaskTide's development did not want constrain this area.
</p>

<p>
With the jnosql-communication, and jnosql-mapping JARs packaged into TaskTide. The required driver must be installed, as fetching the JAR from <a href="https://mvnrepository.com/artifact/org.eclipse.jnosql.databases/jnosql-mongodb/1.1.6">Maven Central</a> will not include the dependancies that that driver uses. While an example is provided for <a href="https://github.com/eclipse-jnosql/jnosql-databases/tree/main?tab=readme-ov-file#oracle-nosql">Oracle NoSQL</a>, building from source with Gradle/Maven is outside the scope of this documentation and is not supported.
</p>
<br>

```bash
# Fetch the pom.xml
curl -so pom.xml https://repo1.maven.org/maven2/org/eclipse/jnosql/databases/jnosql-oracle-nosql/1.1.9/jnosql-oracle-nosql-1.1.9.pom

# Fetch dependancies: Oracle's Driver is nosqldriver-5.4.17.jar
mvn dependency:copy-dependencies -DoutputDirectory=./oracle

# Move all jars to lib
mv ./oracle/*jars tasktide-0.9.0/lib/

# Fetch the JNoSQL Oracle Driver
curl -so tasktide-0.9.0/lib/jnosql-oracle-nosql-1.1.9.jar https://repo1.maven.org/maven2/org/eclipse/jnosql/databases/jnosql-oracle-nosql/1.1.9/jnosql-oracle-nosql-1.1.9.jar
```
