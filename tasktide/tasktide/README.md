# TaskTide - ClientApp
<p id="intro">
Orchestrates the TaskTide-ItemStore, TaskTide-CoreLib, and TaskTide-Engine into a configuarable command-line application. Whose design caters for the development of future clients (ex REST-API) through the TaskTide-Client interface (<a href="/tasktide/tasktide/README.md#3-client-arcitecture">shown here</a>). How the program runs is goverened by arguments that are supplied at runtime, or the use of a "<a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties" >TaskTide Configuration File</a>". This design choice was to allow users of different familiarities to be able to run the program. Though not recommended to use both where not required, command-line arguments overwrite the MPC values, and the global command-line arguments also include documentation on database backend for reference.

---
## 1). Configuring TaskTide

#### TaskTide Configuration File
<p id="config-file">
The complete configuration for TaskTide is <a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">provided here</a>. For clarity this shows all values, but not all of the supplied are required. For instance, if using an RocksDB/SQLite backend then neither, the Relational/SQL/JPA backends, or the NoSQL configurations are not needed. Similarly if using Relational/SQL/JPA backends, no configurations are required for RocksDB/SQLite, or NoSQL (vice versa). Simiarlly, if running the EngineClient is the requirement, then the ManagerClient configs are needed and vice versa.
</p>

<p id="db-config">
If using an SQL, or NoSQL backend then a microprofile-configuration file like the referenced <a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">TaskTide Config File</a>, defined by <a href="https://smallrye.io/smallrye-config/Main/config/getting-started/">SmallRyeConfig</a> must be used. Additionally, SQL databases also require the use of a Java Persistence API XML config like that <a href="/tasktide/tasktide/src/main/resources/META-INF/persistence.xml">linked here</a>. How to configure backend database for TaskTide is <a href="/tasktide/tasktide/README.md#a-global-configurations">described here<a>.
</p>

---
#### Command-Line Arguments
<p id="command-line-config">
Command-line arguments are used to configure the client to run such as the Engine for task processing, or the Manager for the registration, and management of tasks. With this, TaskTide has properties that are configured "<i>globally</i>" like the specific backend to use, that are common for both the Manager and Engine. In addition to this, each client has their own configuration that specific to it. For instance the Manager client has input/output files to coordinate its import/export operations. Whereas the Engine, has arguments for the number of threads to use for the parallel processing of TaskTide entities. The complete command-line arguments can be found by running "tasktide --help/-h". The <a>following link</a> directs to table text showing the same.
</p>

---
## 2). TaskTide Configurations
<p id="config">
The table below maps TaskTide configuration parameters from config file, to command-line arguments (where appropriate). Which has been separated into its separate componenets being "<i>1). Global</i>" which defines client, and backend database type to use. "<i>2). Manager</i>" for task scheduling/CRUD, and "<i>3). Engine</i>" for task processing. The global command-line arguments also include documentation on database backend for reference. 
</p>

---
### a). Global Configurations
| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Client | Defines which client to use | Manager/Engine | tasktide.client | -c/--client
| Repository Type | Defines which backend repository to use | NoSQL/SQL/RocksDB/SQLite | tasktide.core.repository.type | -rt--repository-type
| File Path | Used in conjuction with Repository Type for ItemStore databases (RocksDB/SQLite) for directory where data is stored | ~/path/To/My/ItemStore | tasktide.core.repository.file-path | -fp/--file-path
| Date Format | Format to use for Date strings, defaulted | dd/MM/yy HH:mm:ss | tasktide.utils.date-format | -df/--date-format

---
#### i). NoSQL Backend Configurations
<p id="nosql-config">
Note that the following is a minimal example for "<i><a href="https://couchdb.apache.org/">couchDB</a></i>", and should not be present in the "<i><a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">TaskTide Config File</a></i>" if either an SQL, or ItemStore backend are being used. Full NoSQL configurations can be found at the corresponding project "<i><a href="https://github.com/eclipse-jnosql/jnosql-databases">linked here</a></i>". Lastly, the following guide describes how to incorporate NoSQL database into TaskTide (need a build & install for that GH repo).
</p>

| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Database | Defines which database to use for persisting "<i>Workflows, Steps, and WorkItems</i>" | tasktide | jnosql.document.database | "<i><b>NA</b></i>" |
| Provider | Defines which database driver to use | org.eclipse.jnosql.databases.couchdb.communication.CouchDBDocumentConfiguration | jnosql.document.provider | "<i><b>NA</b></i>" |
| Host | Database host | localhost | jnosql.couchdb.host | "<i><b>NA</b></i>" |
| Port | The port on the configured host listening for client connections | 5439 | jnosql.couchdb.port | "<i><b>NA</b></i>" |
| Username | Username to use for authenticating requests | canBeSetAsAnEnvironmentalVariable | jnosql.couchdb.username | "<i><b>NA</b></i>" |
| Password | Password to use for authenticating user | canBeSetAsAnEnvironmentalVariable | jnosql.couchdb.password | "<i><b>NA</b></i>" |

---
#### ii). Relational Backend Configurations
<p id="sql-config">
Relational database management system/SQL support is provided through <a href="https://www.baeldung.com/learn-jpa-hibernate">JPA-Hibernate</a> using <a href="https://www.baeldung.com/hikaricp">Hikari Data Source</a>. Where a single "<i><a href="https://jakarta.ee/specifications/persistence/2.2/apidocs/javax/persistence/entitymanager">Entity Manager</a></i>" per instance provides "<i>Workflows, Steps, and WorkItems</i>" persistence. In order to use this interface, the implementations must be configured being "<i>Hikari CP</i>", and "<i>Hibernate</i>". As with the <a href="/tasktide/tasktide/README.md#nosql-backend-configurations">NoSQL Configurations</a>, if a relational backend is being used. Then neither the ItemStore, nor the JNoSQL configurations need to be defined. The configurations provided below are a minimal parameters for use with <a href="https://mariadb.org/">MariaDB</a>. 
</p>

| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Database URL | Defines the database to use for persisting "<i>Workflows, Steps, and WorkItems</i>" | jdbc:mysql://localhost:3306/tasktide_database | datasource.user | "<i><b>NA</b></i>" |
| Provider | Defines which database driver to use | com.mysql.cj.jdbc.Driver | datasource.driver | "<i><b>NA</b></i>" |
| Username | Username to use for authenticating requests | canBeSetAsAnEnvironmentalVariable | datasource.user | "<i><b>NA</b></i>" |
| Password | Password to use for authenticating user requests | canBeSetAsAnEnvironmentalVariable | datasource.password | "<i><b>NA</b></i>" |
| Dialect | SQL-JDBC bridge | org.hibernate.dialect.MariaDBDialect | hibernate.dialect | "<i><b>NA</b></i>" |
| DDL Auto | Schema generation tool see hibernate documentation <a href="https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch03.html#configuration-misc-properties">linked here</a> | update | hibernate.hbm2ddl.auto | "<i><b>NA</b></i>" |


---
### b). Engine Client Configurations
<p id="engine-client">
The engine client brings in parallel task processing over the configured backend, with real-time updates being applied to throughout the life-cycle of a task. The below parameters can be used to adjust how TaskTide processes these tasks, such as which task collection, level of parallelism, its monitoring componenets etc. The only mandatory property is the Step property, which when a comma separated list is provided processes tasks from those workflow steps.
</p>

| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Step | Defines which Step(s) to process | StepA,ThenStepB,ThenStepC | tasktide.engine.step | -st/--step |
| WorkItem Threads | Defines the number of threads to recruit for WorkItem processing | 2 | tasktide.engine.worker.processor.threads.workitem | -w/--work-item-threads |
| WorkItem Threshold | Defines the processing threshold for WorkItem | 2 | tasktide.engine.worker.processor.threshold.workitem | -ws/--work-item-sub-task-threshold |
| ItemTask Threads | Defines the number of threads to recruit for ItemTask processing | 2 | tasktide.engine.worker.processor.threads.itemtask | -i/--item-task-threads |
| ItemTask Threshold | Defines the processing threshold for ItemTask | 2 | tasktide.engine.worker.processor.threshold.itemtask | -is/--item-task-sub-task-threshold |
| TimeKeeper Level | Configures whether TimeKeeper Observer is optional | 1/0 | tasktide.engine.observer.timekeeper | -tk/--time-keeper |
| TimeKeeper onStart | Configures whether TimeKeeper's onStart method can fail | true/false | tasktide.engine.observer.timekeeper.onStart | -tks/--time-keeper-onStart |
| TimeKeeper onProcessing | Configures whether TimeKeeper's onProcessing method can fail | true/false | tasktide.engine.observer.timekeeper.onProcessing | -tkp/--time-keeper-onProcessing |
| TimeKeeper onEnd | Configures whether TimeKeeper's onEnd method can fail | true/false | tasktide.engine.observer.timekeeper.onEnd | -tkse/--time-keeper-onEnd |

---
### c). Manager Client Configurations
<p id="manager-client">
The manager client brings in task scheduling using the configured backend. Operations performed the Manager open these CURD actions via the configurable properties described below. While the TaskTide-ManagerClient can be used within ETL scripts to enqueue the next step for an active item, it's recommended to import through the file import (<a href="/tasktide/tasktide/src/main/resources/nestedTaskImports.txt">example provided here</a>). 
</p>

| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Target | Defines the Target entity for required CRUD operation | Workflow/Step/WorkItem | tasktide.manager.target | -tgt/--target |
| Target Step | Defines the target step | myStep | tasktide.manager.targetStep | -ts/--target-step |
| Method | Defines manager method to run | Import/Export | tasktide.method | -m/--method |
| Input File | Defines the full input file path for import | ~/myData.txt | tasktide.manager.inputFile | -i/--input-file |
| Delimiter | Defines field delimiter of the input file | ',' OR '\t' | tasktide.manager.delimiter | -d/--delimiter |
| Nested Delimiter | Defines delimiter of tasks if provided | ':' OR '/' | tasktide.manager.nestedDelimiter | -nd/--nested-delimiter |
| Output File | Defines full file path for export JSON formatted | ~/myExport.txt | tasktide.manager.outputFile | -of/--output-file |

---
### 3). Client Arcitecture
<p align="center">
  <img src="/tasktide/docs/uml/client-app.svg" alt="ClientApp-UML"/>
</p>