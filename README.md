<p align="center">
  <img src="/tasktide/docs/assets/logo1.jpg" alt="TaskTide Logo" width="300"/>
</p>

# TaskTide

![Build](https://img.shields.io/badge/build-passing-brightgreen)  
![License](https://img.shields.io/badge/license-Apache%202.0-blue)  

**TaskTide** is a modular, **Pilot Job System** designed for modern **HPC**, **Grid**, and **Edge Computing** workloads. It enables execution of **ETL-style workflows** using dynamic tasks, and supports both NoSQL (ex MongoDB, CouchDB, Cassandra, RocksDB etc), and SQL (ex MySQL, Postgres, Microsoft SQL Server etc) database backends. The unit of work being a script/executable allows TaskTide to support scale out of any provided workload such as arbitrary or pipelined shell/R/Python/Spark scripts, and decouples environment configuration from TaskTide deployment.

---

## 🚀 Features

- 🛠️ **Pilot Job Execution Model** – Tasks are dynamically scheduled and executed inside long-running jobs.
- 🔄 **ETL-Friendly**: Tasks are treated as extraction, transformation, or loading scripts/programs.
- <img src="/tasktide/docs/assets/database.png" alt="Database Icon from 'https://www.flaticon.com/free-icons/database'" width="18"/> **Backend Agnostic** – Works with Document (e.g. MongoDB), Embedded (e.g. RocksDB), Key-Value (e.g. Redis), and Relational (e.g Postgres) stores.
- 💻 **Native Task Execution** – Runs any local or system executable/script.
- 🔀 **Nested Workflow Modeling** – Compose tasks into hierarchical workflows using a flexible domain model.
- 🧪 **Tested**: Built with CI/CD, Docker support, and integration tests across database types.

---

## 🧑‍💻 Getting Started
<p>
It is recommended to download & unpack TaskTide releases, and install them into standard directories depending on OS (ex '/usr/lib'). Backend database configurations should follow providers recommendation, since <a href="https://github.com/eclipse-jnosql/jnosql-databases">Jakara NoSQL</a> brings in NoSQL support, and <a href="https://www.baeldung.com/learn-jpa-hibernate">JPA-Hibernate</a> using <a href="https://www.baeldung.com/hikaricp">Hikari Data Source</a> brings in SQL, these are acknowledged <a href="/tasktide/core/README.md">here</a>. The documentation is focused on the client app TaskTide.
</p>


#### a). Fetch & Unpack release
```bash
# 1). Fetch zips
curl -so tasktide.zip https://github.com/BrenKenna/TaskTide/releases/download/v0.9.0/tasktide.zip

# 2). Unpack
unzip tasktide.zip && rm -f tasktide.zip
```

#### b). Running TaskTide
<p>
How TaskTide should run can be configured based on parameters in a <a href="/tasktide/tasktide/src/main/resources/META-INF/microprofile-config.properties">config file</a>, or command-line arguments. This was to simplify the use case of the Engine and Manager clients, as they are target orientated. However, when using command-line arguments the target backend parameters must be declared in that file as they are set and provided by the Jakarta-NoSQL, and JPA dependancies (if being used). Additionally since only one backend database type is recommended, application runtime can be optimized by removing unused dependancies (ex JNoSQL if JPA etc) <a href="/tasktide/core/README.md">described here</a>. 
</p>

```bash
# 3). Run TaskTide
./tasktide/bin/tasktide

# --- OR ---
./tasktide/bin/tasktide <client: Manager | Engine> <client args: -h/--help>
```

---

## 🧱 Architecture

- **Core Model**             – Defines the stateful task and workflow data structure, described <a href="/tasktide/core/README.md">here</a>.
- **Engine Lib**             – Processes and tracks WorkItems and their tasks, described <a href="/tasktide/engine/README.md">here</a>.
- **Client Application**     – Provides access and services for workflows and persistence, described <a href="/tasktide/engine/README.md">here</a>.


<p align="center">
  <img src="/tasktide/docs/assets/tasktide-db-hook.png" alt="TaskTide Architecture"/>
</p>