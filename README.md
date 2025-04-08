<p align="center">
  <img src="docs/assets/logo1.png" alt="TaskTide Logo" width="300"/>
</p>

# TaskTide

![Build](https://img.shields.io/badge/build-passing-brightgreen)  
![License](https://img.shields.io/badge/license-Apache%202.0-blue)  

**TaskTide** is a modular, extensible **Pilot Job System** designed for modern **HPC**, **Grid**, and **Edge Computing** workloads. It enables execution of **ETL-style workflows** using dynamic tasks and supports **SQL**, **NoSQL**, and **Key-Value** database backends.

---

## 🚀 Features

- 🛠️ **Pilot Job Execution Model** – Tasks are dynamically scheduled and executed inside long-running jobs.
- 🔄 **ETL-Friendly**: Tasks are treated as extraction, transformation, or loading scripts/programs.
- <img src="docs/assets/database.png" alt="Database Icon from 'https://www.flaticon.com/free-icons/database'" width="18"/> **Backend Agnostic** – Works with Document (e.g. MongoDB), Relational (e.g. PostgreSQL), and Key-Value (e.g. Redis) stores.
- 💻 **Native Task Execution** – Runs any local or system executable/script.
- 🔀 **Nested Workflow Modeling** – Compose tasks into hierarchical workflows using a flexible domain model.
- 🌐 **REST & gRPC Interfaces** – Remote control and orchestration through pluggable APIs.
- 🐳 **Dockerized Services** – Easily deploy engine, REST, and gRPC modules as containers.
- 📦 **Maven Ready** – Available on Maven Central for easy integration.
- 🧪 **Tested**: Built with CI/CD, Docker support, and integration tests across database types.

---

## 🧱 Architecture

```text

- **Core Model** – Defines the stateful task and workflow data structure.
- **Engine** – The CLI-based executor that processes and tracks WorkItems and their tasks.
- **Manager** – Provides access and services for workflows and persistence.
- **REST API & gRPC** – Interfaces for external control and integration.

+------------------+     +-------------------+     +--------------------+
|   REST / gRPC    | <-- |      Manager      | <-- |      Database      |
|    Interface     |     |  (Orchestration)  |     | (SQL / NoSQL / KV) |
+------------------+     +-------------------+     +--------------------+
          |
          v
+-------------------+
|      Engine       |
|  (Task Executor)  |
+-------------------+
