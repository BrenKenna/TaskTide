<p align="center">
  <img src="/tasktide/docs/assets/logo1.jpg" alt="TaskTide Logo" width="300"/>
</p>

# TaskTide

![build](https://img.shields.io/badge/build-passing-brightgreen)  
![license](https://img.shields.io/badge/license-Apache%202.0-blue)  

<p id="intro-a">
<strong>TaskTide</strong> is a modular <strong>Workflow Orchestration Engine</strong> designed for modern <strong>HPC</strong>, <strong>Grid</strong>, <strong>Edge Computing</strong> workloads. It enables the execution of <strong>ETL-style workflows</strong> and arbitrary <strong>Data Application</strong> collections as tasks.
</p>
<br>

<p id="intro-b">
By modelling <strong>Workflow</strong>, and <strong>Execution States</strong> as first-class orchestration entities. TaskTide provides its users with real-time <em>workflow registration</em>, <em>introspection</em>, and <em>lifecycle influence</em> at runtime while task are actively consumed across distributed compute resources.
</p>
<br>

<p id="intro-c">
TaskTide ships as a <strong>lightweight</strong>, <strong>daemon-less</strong>, <strong>configurable</strong> approach for workflow scale-outs that decouples <em>Workflow Orhcestration</em> logic from <em>Infrastructure Specific</em> backends. Supporting <strong>Relational</strong> (<em>Postgres, Maria, MySQL, Microsoft, Oracle etc</em>), <strong>Non-Relational</strong> (<em>MongoDB, CouchDB, Oracle etc</em>) database management systems, and <strong>daemon-less</strong> databases (<em>SQLite, RocksDB</em>) reflecting its backend-agnostic design.
</p>
<br>

---

## 🚀 Features

<p id="features">

- 🛠️ **Pilot Job Execution Model** – Tasks are dynamically scheduled and executed inside long-running jobs.
- 🔄 **ETL-Friendly**: Tasks are treated as extraction, transformation, or loading scripts/programs.
- <img src="/tasktide/docs/assets/database.png" alt="Database Icon from 'https://www.flaticon.com/free-icons/database'" width="18"/> **Backend Agnostic** – Works with Document (e.g. MongoDB), Daemon-less (e.g. RocksDB, SQLite), Key-Value (e.g. Redis), and Relational (e.g Postgres) stores.
- 💻 **Native Task Execution** – Runs any local or system executable/script.
- 🔀 **Nested Workflow Modeling** – Compose tasks into hierarchical workflows using a flexible domain model.
- 🧪 **Tested**: Built with CI/CD, Docker support, and integration tests across database types.
</p>
<br>

---

## 🧑‍💻 Getting Started
<p id="getting-started-a">
An installation guide tailored to variety of use-cases is <a href="/Install.md">provided here</a>. Backend database configurations should follow provider recommendations, since <a href="https://github.com/eclipse-jnosql/jnosql-databases">Jakara NoSQL</a> brings in NoSQL support, and <a href="https://www.baeldung.com/learn-jpa-hibernate">JPA-Hibernate</a> using <a href="https://www.baeldung.com/hikaricp">Hikari Data Source</a> brings in SQL, whose use for TaskTide are <a href="/tasktide/tasktide/README.md#a-global-configurations">documented here</a>.
</p>
<br>

---

#### Running TaskTide
<p id="getting-started-b">
How TaskTide should run can be configured based on parameters in a <a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">TaskTide Config File</a>, or command-line arguments. This was to simplify the use case of the Engine and Manager clients, as they are target orientated. However, when using command-line arguments the target backend parameters must be declared in that file as they are set and provided by the Jakarta-NoSQL, and JPA dependancies (if being used). Additionally since only one backend database type should be used, application runtime can be optimized by removing unused dependancies (ex JNoSQL if JPA etc) <a href="/tasktide/tasktide/README.md#a-global-configurations">described here</a>.
</p>

```bash
# Run using parameters from TaskTide config file
./tasktide/bin/tasktide

# --- OR ---
./tasktide/bin/tasktide <client: Manager | Engine | API> <client args: -h/--help>
```
<br>

---

## 🧱 Architecture

<p id="arch-a">

- **Core Model**             – Defines the stateful task and workflow data structure, described <a href="/tasktide/core/README.md">here</a>.
- **Engine Lib**             – Defines the task processing and tracking logic for WorkItems and their tasks, described <a href="/tasktide/engine/README.md">here</a>.
- **Web API**                – Defines Jakarta-WS REST API, and an embedded Jetty-WebServer, described <a href="/tasktide/api/README.md">here</a>.
- **Mutex**                  - Defines ItemSore semaphore for acquiring a mutex on the configured RocksDB/SQLite database, described <a href="/tasktide/mutex/README.md">here</a>.
- **ItemStore**              - Defines an interface for configuring TaskTide with daemonless databases (RocksDB/SQLite), described <a href="/tasktide/itemstore/README.md">here</a>.
- **Parser**                 - Defines a configurable command-line argument tree for TaskTide, described <a href="/tasktide/parser/README.md">here</a>.
- **Client Application**     – Provides access and services for workflow deployments and persistence, described <a href="/tasktide/tasktide/README.md">here</a>.
</p>
<br>
<br>

<p id="arch-b" align="center">
  <img src="/tasktide/docs/assets/tasktide-db-hook.png" alt="TaskTide Architecture"/>
</p>