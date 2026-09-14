# Using Embedded Databases for TaskTide

Since common RDBMS and NoSQL databases operate as services that expose a port, TaskTide wanted to support use cases where such service is no configurable by the end-user (ex multi-tenant HPC environment).

In these instances TaskTide's ItemStore interface was designed and currently supports RocksDB, and SQLite (both drivers are supplied). The ItemStore uses a de-centralized leader election workflow to coordinate read/write operation queue to maintain, so that the end-user is not requried to provision a side-car job to manage this.

Please Note ***It is not recommended to operate multiple database technologies with TaskTide***. The data for the ItemStore repository is persisted under a user-provided directory, while backup and retrival of this directory is not the responsibility of TaskTide. Backup and retrival can be done by archiving the configured directory (ex tar -czvf ItemStore-Repo.tar.gz ItemStore-Repo).

The following describes:
<ul>
    <li>Using the ItemStore backend for TaskTide</li>
    <li>Applying ItemStore configurations for TaskTide</li>
</ul>


# 1). Using the ItemStore backend for TaskTide

Currently the [RocksDB](https://rocksdb.org/), and [SQLite](https://sqlite.org/) embedded databases are supported by TaskTide. These APIs in addition to the TaskTide-ManagerAPI are available to users.

TaskTide uses a de-centralized semaphore for coordinating distributed read/writes, and separates each "***Workflows***, ***Steps***, and ***WorkItems***" data model to cater for future development. Meaning that core configurations of the ItemStore repository, and Mutex directories are stable, but specifics around them and directory structure is subject to change.

---

# 2). Applying ItemStore configurations for TaskTide

Database configuration is a ***global setting*** for TaskTide because all of the TaskTides API use it as their coordination layer directly or indirectly. The Manager-API provides the TaskTide repository and CRUD operations against it, that the Engine-API uses for workload acquisition, and registering processing lifecycle events, and the Web-API by exposing the Manager-API through a RESTful interface.


| Property | Use | Example Value(s) | Config Parameter | Command-Line Parameter |
|--|--|--|--|--|
| Repository Type | Defines which backend repository to use | RocksDB/SQLite | tasktide.core.repository.type | -rt--repository-type |
| File Path | Used in conjuction with Repository Type for ItemStore databases (RocksDB/SQLite) for directory where data is stored | ~/path/To/My/ItemStore | tasktide.core.repository.file-path | -fp/--file-path |
