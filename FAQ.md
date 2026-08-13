# Frequently Asked Questions

## 1). How do install TaskTide?
<p>
Download the latest release <a href="https://github.com/BrenKenna/TaskTide/releases">linked here</a>. Also note, that TaskTide is a Java program so a Java Runtime is required to run it and was developed with <a href="https://www.oracle.com/europe/java/technologies/downloads/#java21">Java-21</a>. Insight on backend setup is [provided here](Database-Driver-Installation.md#database-driver-installation).
</p>
<br>


## 2). What backends are usable?
<p>
In short any, use depends on environment where TaskTide is implemented (ie containerized, Grid, HPC, Edge). Recognizing that not all environments can utilise the same "<i>single source of truth</i>", support for different backends was baked into its design. For environments where a production grade database is available over a network either [Relational Databases](tasktide/tasktide/README.md#ii-relational-backend-configurations) like MySQL/Postgres, or [NoSQL Databases](tasktide/tasktide/README.md#i-nosql-backend-configurations) like MongoDB, CouchDB etc can be used. For environments like HPC/Edge which are constrained to daemonless databases, an [ItemStore](tasktide/tasktide/README.md#a-global-configurations) can be configured using either SQLite or RocksDB.
</p>
<br>


## 3). How do I configure a backend for TaskTide?
<p>
While database provisioning is outside the scope of TaskTide, [this link](Database-Driver-Installation.md) offers a guide on Relational and NoSQL databases, and configuration of RocksDB/SQLite is [described here](/tasktide/tasktide/README.md#a-global-configurations).
</p>
<br>


## 4). What exactly are these ETLs and Pilot Jobs?

### a). ETL Scripting
<p>
An ETL is a design pattern for data processing, which organizes data processing into three steps. The first step "<i>Extraction</i>" stages data to be processed onto the compute platform, for instance for a docker container this would be downloading source data/firing a query against a database. The second step "<i>Transformation</i>" performs the required action over input data. The third step "<i>Loading</i>" is where the results from this process are pushed to storage for downstream analysis.
  
TaskTide recommends to frame tasks as ETL scripts to support its best use, automation, fault-tolerance, and observability. Designing a collection of related ETL scripts is called pipelining, where the pipeline produces the required output, each step in that workflow acts as checkpoints for data migrating through this process. Note worthy aspects this approach to consider include the wall-time of an analytical pipeline steps (could "work"  be chopped up), environmental requirements pipeline such as any reference data and resource utilization (memory, cpu etc), and dimensions of input data that support logical data subsetting and aggregation.
</p>
<br>

### b). Pilot Jobs
<p>
A pilot job is a design approach in batch computing, where a set of related long running batch jobs process their associated units of work. Instead of say, an array short/long running jobs fired independently. Together the two approaches act as a means of orchestration batch job deployments, where ETLs can be designed & tested on smaller subsets, and scaled out with TaskTide.
  
There are two scale out models in this approach called early and late task binding, both of which are supported by TaskTide. In early-task binding tasks are allocated to pilot before that job becomes active, be likened to fetch and run batch jobs. Late task binding would be more similar to interactive computing like with notebooks, where a pilot job is provisioned ahead of time and assigned work dynamically. TaskTide supports these task binding semantics through its ETL model, model annotations, workers fetching targetted work collections from central database, and configurable batch/service execution polocies of the TaskTide-Engine. 
</p>
<br>


## 5). What environments does TaskTide support?
<p>
Depends on what you mean by environment. TaskTide has been tested on both windows & linux operating systems, as well as containerized & HPC platforms.
</p>
<br>


## 6). Can I use MySQL, PostgresSQL, Microsoft SQL Server, Oracle?
<p>
Yup, [see here](tasktide/tasktide/README.md#ii-relational-backend-configurations) and [here](Database-Driver-Installation.md#1-install-required-relational-database-drive) for configuration guide.
</p>
<br>


## 7). Can I use NoSQL Databases like MongoDB, CouchDB, Oracle?
<p>
Yup, [see here](tasktide/tasktide/README.md#i-nosql-backend-configurations) and [here](Database-Driver-Installation.md#2-using-pre-packaged-nosql-database-driver) for configuration guide.
</p>
<br>


## 8). I want to use SQLite/RocksDB, do I need to install any drivers for them?
<p>
Drivers for these are provided with TaskTide. Just specify which one you're using, and the fully qualified path. Tasktide does the rest.
  
It is recommended to copy the folder temporarily for any live monitoring during production scale out
</p>
<br>


## 8). How do I use TaskTide?
<p>
TaskTide is designed as configurable command-line client where its [Manager Client](tasktide/tasktide/README.md#c-manager-client-configurations) for task orchestration, and its [Engine Client](tasktide/tasktide/README.md#b-engine-client-configurations) can used for task processing. See [this link](FAQ.md#1-how-do-install-tasktide) for installation. Templates will be provided to demonstrate TaskTide deployment.
</p>
<br>