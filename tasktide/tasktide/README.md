## TaskTide - ClientApp
<p>
Orchestrates the TaskTide-ItemStore, TaskTide-CoreLib, and TaskTide-Engine into a runnable command-line application. Where the design caters for the development of future clients (ex REST-API) through the TaskTide-Client interface (architecture shown below).
</p>


### 1). Configuring TaskTide

#### x). Core Componements
<p>
TaskTide is designed to run as a configurable command-line program. Meaning that how to run the program is dictated by the arguments supplied at runtime, or the use of a "<a href="https://download.eclipse.org/microprofile/microprofile-config-2.0/microprofile-config-spec-2.0.html" MicroProfile-Configuration (MPC)>". This design choice was to allow users of different familiarities to be able to run the program. Though not recommended to use both where not required, command-line arguments overwrite the MPC values.
</p>


##### -). MicroProfile Configuration
<p>
The complete MPC for TaskTide is <a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">shown here</a>. For clarity this shows all values, but not all required. For instance, if using an RocksDB/SQLite backend then neither, the Relational/SQL/JPA backends, or the NoSQL configurations are not needed. Similarly if using Relational/SQL/JPA backends, no configurations are required for RocksDB/SQLite, or NoSQL (vice versa). Simiarlly, if running the EngineClient is the requirement, then the ManagerClient configs are needed and vice versa.
</p>


##### -). Command-Line Arguments
<p>Command-line arguments are used to configure the client to run such as the Engine for task processing, or the Manager for the registration, and management of tasks. With this, TaskTide has properties that are configured "<i>globally</i>" like the specific backend to use, that are common for both the Manager and Engine. In addition to this, each client has their own configuration that specific to it. For instance the Manager client has input/output files to coordinate its import/export operations. Whereas the Engine, has arguments for the number of threads to use for the parallel processing of TaskTide entities.
</p>
<p>
The complete command-line arguments can be found by running "tasktide --help/-h". The <a>following link</a> directs to table text showing the same.
</p>


#### x). Database Configuration
<p>
If using a SQL, or NoSQL backend the a microprofile-configuration file like that in <a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">linked here</a>, and <a href="https://smallrye.io/smallrye-config/Main/config/getting-started/">SmallRyeConfig here</a> must be used. Additionally, SQL databases also require the use of a Java Persistence API XML config like that in the provided <a href="/tasktide/tasktide/src/main/resources/META-INF/persistence.xml">persistence.xml</a>
</p>


#### x). Client Configuration
<p>
TaskTide is composed of different clients, which peform their related actions. For instance, the TaskTide-ManagerClient orchestrates tasks into the desired backend such as File Base with RocksDB/SQLite, Relational/SQL databases, or NoSQL databases like MongoDB, CouchDB, DynamoDB etc. The TaskTideEngine-Client fetches work from the desired database and processes them on the host running that client.
</p>


### x). Client Arcitecture
<p align="center">
  <img src="/tasktide/docs/uml/client-app.svg" alt="ClientApp-UML"/>
</p>