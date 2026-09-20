<p align="center">
  <img src="/docs/assets/logo1.jpg" alt="TaskTide Logo" width="300"/>
</p>

# TaskTide

[![Website](https://img.shields.io/badge/Website-tasktide.org-blue)](https://docs.tasktide.org)
[![Maven Central](https://img.shields.io/maven-central/v/org.tasktide/tasktide)](https://central.sonatype.com/artifact/org.tasktide/tasktide)
[![Documentation](https://img.shields.io/badge/Documentation-docs.tasktide.org-blue)](https://docs.tasktide.org)
[![API Reference](https://img.shields.io/badge/API%20Reference-JavaDoc-blue)](https://api-docs.tasktide.org)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.21959893.svg)](https://doi.org/10.5281/zenodo.21959893)
[![build](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/BrenKenna/TaskTide/actions/workflows/gradle.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](https://github.com/BrenKenna/TaskTide/blob/main/LICENSE)
[![OpenSSF Scorecard](https://api.scorecard.dev/projects/github.com/BrenKenna/TaskTide/badge)](https://scorecard.dev/viewer/?uri=github.com/BrenKenna/TaskTide)


<strong>TaskTide</strong> is a modular <strong>Workflow Orchestration Engine</strong> designed for <strong>Containerized</strong>, <strong>Cloud</strong>, <strong>HPC</strong>, <strong>Grid</strong>, and <strong>Edge Computing</strong> workloads. It enables the execution of <strong>ETL-style workflows</strong> and arbitrary <strong>Data Application</strong> as task collections.
</p>

By modelling <strong>Workflow</strong>, and <strong>Execution States</strong> as first-class orchestration entities. TaskTide provides its users with task <em>registration</em>, real-time <em>workflow introspection</em>, and <em>lifecycle influence</em> as they are actively consumed across distributed compute resources.

TaskTide ships as a <strong>lightweight</strong>, <strong>configurable</strong> solution for workflow orchestration. That decouples <strong>Workflow Orhcestration</strong> logic from <strong>Infrastructure Specific</strong> backends. Supporting its deployment across different infrastructures like HPC, Cloud, and Grid. In additon to <strong>Relational</strong> (<em>Postgres, Maria, MySQL, Microsoft, Oracle etc</em>), <strong>Non-Relational</strong> (<em>MongoDB, CouchDB, Oracle etc</em>) database management systems, and <strong>Embedded</strong> databases (<em>SQLite, RocksDB</em>). Catering for diverse sets of operational needs for data application deployment.

<br>

<p align="center">
  <img src="/docs/assets/tasktide-ops.png" alt="TaskTide database backed producer-consumer operations"/>
</p>

<br>

---

## 🧑‍💻 Getting Started

An installation guide catering for different uses is [provided here](/docs/documentation/INSTALL.md). Backend database configurations should follow provider recommendations, since [Jakara NoSQL](https://github.com/eclipse-jnosql/jnosql-databases) brings in NoSQL support, and [JPA-Hibernate](https://www.baeldung.com/learn-jpa-hibernate) using [Hikari Connection Pool](https://www.baeldung.com/hikaricp) brings in SQL, whose use for TaskTide are documented [here for NoSQL](/docs/documentation/database-configuration/NoSQL-Databases.md), [here for SQL](/docs/documentation/database-configuration/SQL-Databases.md), and [here for ItemStore](/docs/documentation/database-configuration/Embedded-Databases.md) for embedded databases like SQLite. and RocksDB.

How TaskTide should run can be configured based on parameters in a [TaskTide configuration file](/docs/configs/microprofile-config.properties)

<br>

---

## 💻 Running TaskTide

As a Java application TaskTide [requires Java+17](https://docs.oracle.com/en/java/javase/).

```bash
# Run TaskTide container image
docker container run --rm \
    bkenna/tasktide:latest \
        < CLI: Manager | Engine | Web-Api > \
            < CLI Options: >


# -- OR --- Run the required client with provided configs
tasktide \
  < CLI: Manager | Engine | API > \
    < CLI Options: >


# --- OR --- Run using parameters from TaskTide config file
tasktide

```

<br>

---

## 🧰 TaskTide Resources

- 🐳 **[Docker ➞](https://docker.tasktide.org)**
  > Official CI verified TaskTide container image

- 📚 **[TaskTide Documentation ➞](https://docs.tasktide.org)**
  > Human-readable guides, configuration, modules etc

- 📦 **[Maven Central ➞](https://maven.tasktide.org)**
  > Official CI verified TaskTide Maven artifacts

- 🌊 **[Use Cases ➞](https://use-cases.tasktide.org)**
  > Community-centric adoptions of TaskTide

- 🧩 **[Java API Documentation ➞](https://api-docs.tasktide.org)**
  > TaskTide JavaDocs site

- 🚀 **[Releases ➞](https://github.tasktide.org/releases)**
  > Downloadable TaskTide releases

<br>

---

## 🚀 Features

- 🛠️ **Pilot Job Execution Model**:     Tasks are dynamically scheduled and executed inside long-running jobs.

- 🔄 **ETL-Friendly**:                  Tasks are treated as extraction, transformation, or loading scripts/programs.

- <img src="/docs/assets/database.png" alt="Flaticon database" width="18"/> **Backend Agnostic**:       Works with Document (e.g. MongoDB), Daemon-less (e.g. RocksDB, SQLite), Key-Value (e.g. Redis), and Relational (e.g Postgres) stores.

- 💻 **Native Task Execution**:         Runs any local or system executable/script.

- 🔀 **Nested Workflow Modeling** :     Compose tasks into hierarchical workflows using a flexible domain model.

- 🐳 **Cloud-native**: Supports execution of containerized workloads.

- 🧪 **Tested**:                        Built with CI/CD, Docker support, and integration tests across database types.

<br>

---

## 🧱 Architecture

- **Core Model**             – Defines the stateful task and workflow data structure [described here](/tasktide/core/).

- **Engine Lib**             – Defines the task processing and tracking logic for WorkItems and their tasks [described here](tasktide/engine/).

- **Web API**                – Defines Jakarta-WS REST API, and an embedded Jetty-WebServer [described here](tasktide/api/).

- **Mutex**                  - Defines ItemStore semaphore for acquiring a mutex on the configured RocksDB/SQLite database [described here](tasktide/mutex/).

- **ItemStore**              - Defines an interface for configuring TaskTide with daemonless databases (RocksDB/SQLite) [described here](tasktide/itemstore/).

- **Parser**                 - Defines a configurable command-line argument tree for TaskTide [described here](tasktide/parser/).

- **Client Application**     – Provides access and services for workflow deployments and persistence [described here](tasktide/tasktide/).

<br>
<br>

<p id="arch-b" align="center">
  <img src="/docs/assets/tasktide-db-hook.png" alt="TaskTide Architecture"/>
</p>
