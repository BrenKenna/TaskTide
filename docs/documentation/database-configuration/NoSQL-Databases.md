# Using NoSQL Databases for TaskTide

The following relates to NoSQL databases supported by [Jakarta-NoSQL Implementations](https://github.com/eclipse-jnosql/jnosql-databases) only, meaning this can be ignored if either RocksDB/SQLite are being used.

A collection of Jakarta-NoSQL database drivers has already been included within TaskTide. These are MongoDB and CouchDB for DocumentTemplate, Cassandra for ColumnTemplate, Redis and DynamoDB for KeyValueTemplate.

In the event that other database drivers are needed, guidance on how they can be used are also described in this document.

Please Note ***it is not recommended to operate multiple database technologies with TaskTide, and provisioning a production NoSQL database is outside the scope of this document***.

The following describes:
<ul>
    <li>Provsion an Ephemeral NoSQL backend for TaskTide</li>
    <li>Using a pre-packaged NoSQL backend for TaskTide</li>
    <li>Using a separate NoSQL backend for TaskTide</li>
    <li>Applying configurations to the TaskTide config file</li>
</ul>

<br>

---

<br>

## 1). Provision an Ephemeral NoSQL Database

The below spins up a temporary couchDB document database.

```bash
docker container run --rm \
    --name couchDB \
    --detach \
    -e COUCHDB_USER=admin \
    -e COUCHDB_PASSWORD=password \
    -p 5984:5984 \
    couchdb:latest

curl -s \
    -X PUT \
http://admin:password@localhost:5984/tasktide_database

```

<br>

---

<br>

## 2). Using Another NoSQL-Database Driver

Although the jnosql-communication, and jnosql-mapping JARs are all packaged into TaskTide. The required driver and its dependancies must still be installed, as fetching the JAR from [Maven Central](https://mvnrepository.com/artifact/org.eclipse.jnosql.databases/jnosql-mongodb/1.1.6) will not include the dependancies that that driver uses.

While building from source with Gradle/Maven is outside the scope of this documentation. An example for fetching a JNoSQL driver and its dependancies are provided for [Oracle NoSQL](https://github.com/eclipse-jnosql/jnosql-databases/tree/main?tab=readme-ov-file#oracle-nosql) to help TaskTide users in this capacity.

```bash
# Fetch the pom.xml
curl \-so pom.xml \
    https://repo1.maven.org/maven2/org/eclipse/jnosql/databases/jnosql-oracle-nosql/1.1.9/jnosql-oracle-nosql-1.1.9.pom

# Fetch dependancies: Oracle's Driver is nosqldriver-5.4.17.jar
mvn \
    dependency:copy-dependencies \
    -DoutputDirectory=./oracle

# Move all jars to lib
mv ./oracle/*jars tasktide-0.9.0/lib/

# Fetch the JNoSQL Oracle Driver
curl -so tasktide-0.9.0/lib/jnosql-oracle-nosql-1.1.9.jar \
    https://repo1.maven.org/maven2/org/eclipse/jnosql/databases/jnosql-oracle-nosql/1.1.9/jnosql-oracle-nosql-1.1.9.jar
```

<br>

---

<br>

## 3). Applying configurations to the TaskTide config file

Database configuration, below table, is a ***global setting*** for TaskTide because each TaskTide use it as their persistence layer. The Manager-API provides the TaskTide repository and CRUD operations against it, that the Engine-API uses for workload acquisition, and registering processing lifecycle events, and the Web-API by exposing the Manager-API through a RESTful interface.

Note that the following is a minimal example for [couchDB](https://couchdb.apache.org/), and should not be present in the [TaskTide Config File](/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties) if either an SQL, or ItemStore backend are being used.

Full NoSQL configurations can be found at the corresponding project [linked here](https://github.com/eclipse-jnosql/jnosql-databases) and are intentionally not bypassed with TaskTide so that the available configurations are still usable.


| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Repository Type | Defines the database backend type to use | tasktide.core.repository.type | NOSQL | "<i><b>NA</b></i>" |
| Jakarta Template Type | Defines the type of Jakarta-NoSQL database type to use ex "<i>Document, KeyValue</i>" | Document/KeyValue | tasktide.core.repository.jnosql.type | "<i><b>NA</b></i>" |
| Database | Defines which database to use for persisting "<i>Workflows, Steps, and WorkItems</i>" | tasktide | jnosql.document.database | "<i><b>NA</b></i>" |
| Provider | Defines which database driver to use | org.eclipse.jnosql.databases.couchdb.communication.CouchDBDocumentConfiguration | jnosql.document.provider | "<i><b>NA</b></i>" |
| Host | Database host | localhost | jnosql.couchdb.host | "<i><b>NA</b></i>" |
| Port | The port on the configured host listening for client connections | 5439 | jnosql.couchdb.port | "<i><b>NA</b></i>" |
| Username | Username to use for authenticating requests | canBeSetAsAnEnvironmentalVariable | jnosql.couchdb.username | "<i><b>NA</b></i>" |
| Password | Password to use for authenticating user | canBeSetAsAnEnvironmentalVariable | jnosql.couchdb.password | "<i><b>NA</b></i>" |
