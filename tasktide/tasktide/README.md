# TaskTide - ClientApp
<p id="intro">
Unified application exposing the TaskTide-Manager, TaskTide-Engine, and embedded TaskTide-WebAPI into a configuarable command-line program. How the program runs is goverened by arguments that are supplied at runtime, or the use of a "<a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">TaskTide Configuration File</a>". This design choice was to allow users of different familiarities to be able to run the program. Though not recommended to use both where not required, command-line arguments overwrite the config file values.
</p>
<br>

---

## 1). Configuring TaskTide

#### TaskTide Configuration File
<p id="config-file">
The complete configuration for TaskTide is <a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">provided here</a>. For clarity this shows all values, but not all of the supplied are required. For instance, if using an RocksDB/SQLite backend then neither, the Relational/SQL/JPA backends, or the NoSQL configurations are not needed. Similarly if using Relational/SQL/JPA backends, no configurations are required for TaskTide-ItemStore/RocksDB/SQLite, or NoSQL etc. Simiarlly, if running the TaskTide-EngineClient is the requirement, then the TaskTide-ManagerClient or WebAPI configs are needed.
</p>

<p id="db-config">
If using an SQL, or NoSQL backend then a microprofile-configuration file like the referenced <a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">TaskTide Config File</a>, defined by <a href="https://smallrye.io/smallrye-config/Main/config/getting-started/">SmallRyeConfig</a> must be used. Additionally, SQL databases also require the use of a Java Persistence API XML config like that <a href="/tasktide/tasktide/src/main/resources/META-INF/persistence.xml">linked here</a>. How to configure backend database for TaskTide is <a href="/tasktide/tasktide/README.md#a-global-configurations">described here<a>.
</p>

---

#### Command-Line Arguments
<p id="command-line-config">
Command-line arguments are used to configure which TaskTide-Client to run such as the Engine for task processing, the Manager for the registration, and management of tasks, or the WebAPI for service deployment. With this, TaskTide has properties that are configured "<i>globally</i>" like the specific backend to use, that are common for both the Manager, Engine, and WebAPI. In addition to this, each client has their own configuration that specific to it. For instance the Manager client has input/output files to coordinate its import/export operations. Whereas the Engine, has arguments for the number of threads to use for the parallel processing of TaskTide entities. The WebAPI, has arguments for configuring IdP. The complete command-line arguments can be found by running "tasktide --help/-h". The <a>following link</a> directs to table text showing the same.
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
| Repository Type | Defines which backend repository to use | NoSQL/SQL/RocksDB/SQLite | tasktide.core.repository.type | -rt--repository-type
| File Path | Used in conjuction with Repository Type for ItemStore databases (RocksDB/SQLite) for directory where data is stored | ~/path/To/My/ItemStore | tasktide.core.repository.file-path | -fp/--file-path
| Date Format | Format to use for Date strings, defaulted | dd/MM/yy HH:mm:ss | tasktide.utils.date-format | -df/--date-format
| Workflow Name | Workflow to target | MyWorkflow | tasktide.core.collection.workflow.name | -wn/--workflow-name
| Step Name | Step to target | MyStep | tasktide.core.collection.step.name | -sn/--step-name
| WorkItem Name | WorkItem to target | MyWorkItem | tasktide.core.collection.work-item.name | -sn/--work-item-collection-name
| Result Set Size | Number of records to restrict repository read operatios to | 10 | tasktide.core.results-set-size | -rss/--result-set-size

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
| Worker Pool Size | Defines the number of engine workers | 2 | tasktide.engine.worker.threads.worker-pool-size | -w/--worker-pool-size |
| ItemTask Threads | Defines the number of threads to recruit for ItemTask processing | 2 | tasktide.engine.worker.threads.itemTask | -i/--item-task-threads |
| Worker Window Size | Defines the number of tasks polled from policy results | 10 | tasktide.engine.worker.window-size | -wws/--work-window-size |
| Lock Wait Time | Configures wait time in seconds for locking an item | 5 | tasktide.engine.worker.lock-wait-time | -l/--lock-wait-time |
| Process Executor Stream Directory | Log stream directory for Process Executor | ~/ | tasktide.engine.process-executor.stream-directory | -sd/--stream-directory |

| Execution Policy | Engine execution policy | BATCH/SERVICE | tasktide.engine.execution-policy | -ep/--execution-policy |
| Strategy Type | Specifies workflow acquisition strategy to use | SEQUENTIAL/ROUND ROBIN | tasktide.engine.policy.acquisition.workflow.strategy | -st/--strategy-type |
| Acquisition Mode | Specifies acqusition mode for workflow strategy | EXHAUST/SCANNER | tasktide.engine.policy.acquisition.workflow.mode | -am/--acquisition-mode |

| Pilot Label Key | CustomAnnotation key on WorkItem for early task binding to pilot job | MyAnnotationKey | tasktide.engine.pilot.label.key | -plk/--pilot-label-key |
| Pilot Label Value | CustomAnnotation value on WorkItem for early task binding to pilot job | MyAnnotationValue | tasktide.engine.pilot.label.value | -plk/--pilot-label-key |
| Pilot Label Annotation | JSON formatted CustomAnnotation | '{ "Key": "Anno Key", "Value": "GPU Target" }' | tasktide.engine.pilot.label.annotation | -pa/--pilot-label-annotation |

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
| ItemId | ItemId over which the required ManagerAction is taken | SomeId | tasktide.manager.itemId | -ii/--item-id |
| Query String | JSON formatted string | '{"Field": "Value"}' | tasktide.manager.queryString | -ii/--item-id |

---

### d). ItemStore Mutex Configuration
<p id="item-store">
The <a href="/tasktide/mutex/README.md">Mutex</a> library is used for as de-centralized operation queue for the <a href="/tasktide/itemstore/README.md">ItemStore Repository</a>.
</p>

| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Mutex Root Directory | Configures root directory for mutex | ~/tasktide/mutex | tasktide.mutex.rootDir | -mrd/--mutex-root-dir |
| Mutex Stale File Threshold | Defines amount of miliseconds active leader is considered stale and deleted | 5 | tasktide.mutex.staleFileThreshold | -sft/--stale-file-threshold |
| Mutex Retry Interval | Configures retry interval for TaskTide-Mutex | 550 | tasktide.mutex.retryInterval | -ri/--retry-interval |
| Mutex Start Jitter | Configures minimum milliseconds wait time | 10-300L | tasktide.mutex.startJitter| -sj/--start-jitter |
| Mutex End Jitter | Configures maximum milliseconds wait time | 301-500L | tasktide.mutex.endJitter| -ej/--end-jitter |
| Min Random Long | Configures value for maximum random long | 10-300L | tasktide.mutex.minRandomLong | -minri/--min-random-long |
| Max Random Long | Configures value for maximum random long | 301-500L | tasktide.mutex.maxRandomLong | -maxri/--max-random-long |

---

### e). Web API
<p id="web-api">
Configurations for the <a href="/tasktide/api/README.md">RESTful API</a>. Further configurations for jersey, and glassfish can be passed down.
</p>

| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Host | Configures host name of WebServer | http://localhost | tasktide.web-api.server.host | -host/--host |
| Port | Port that the webserver is to listen on | 8080 | tasktide.web-api.server.port | -port/--port |
| Base Path | Path that the webserver is mounted onto | tasktide | tasktide.web-api.server.base-path | -bp/--base-path |
