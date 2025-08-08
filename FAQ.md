# Frequently Asked Questions

## 1). How do install TaskTide?
<p>
Download the latest release <a href="https://github.com/BrenKenna/TaskTide/releases">linked here</a>. Also note, that TaskTide is a Java program so a Java Runtime is required to run it and was developed with <a href="https://www.oracle.com/europe/java/technologies/downloads/#java21">Java-21</a>. Insight on backend setup is <a href="/Database-Driver-Installation.md#database-driver-installation">provided here</a>.
</p>


## 2). What backends are usable?
<p>
In short any, use depends on environment where TaskTide is implemented (ie containerized, Grid, HPC, Edge). TaskTide supports is intentionally designed to be deployed environments (ie container, HPC, and Grid). Recognizing that not all environments can utilise the same "<i>single source of truth</i>", support for different backends was baked into its design. For environments where a production grade database is available over a network either <a href="/tasktide/tasktide/README.md#ii-relational-backend-configurations">Relational Databases</a> like MySQL/Postgres can be used, or <a href="/tasktide/tasktide/README.md#i-nosql-backend-configurations">NoSQL Databases</a> like MongoDB, CouchDB etc can be used. For environments like HPC/Edge where batch jobs have direct access to data storage, an <a href="/tasktide/tasktide/README.md#a-global-configurations">ItemStore</a> can be configured using either SQLite or RocksDB.
</p>


## 3). How do I configure a backend for TaskTide?
<p>
While database provisioning is outside the scope of TaskTide, <a href="/Database-Driver-Installation.md">this link</a> offers a guide on Relational and NoSQL databases, and configuration of RocksDB/SQLite is <a href="/tasktide/tasktide/README.md#a-global-configurations">described here</a>
</p>


## 4). What exactly are these ETLs and Pilot Jobs?

### ETL Scripting
<p>
An ETL is a design pattern for data processing, which organizes data processing into three steps. First step is "<i>Extraction</i>" which stages data to be processed onto the compute platform, for instance for a docker container this would be downloading source data/firing a query against a database. Second step is "<i>Transformation</i>", which performs the required action over input data. Third step is "<i>Loading</i>" where the results from this process are pushed to storage for downstream analysis. TaskTide recommends to "<i>phrase</i>" tasks as ETL scripts, where the input changes per task, as such an ETL script is the standardized unit of processing for TaskTide. Designing a collection of related ETL scripts is called pipelining, where the pipeline produces the required output, each step in that workflow acts as checkpoints for data migrating through this process. Reliance of this approach depends on variety of factors which wall-time of an analytical pipeline ("work" could be chopped up), required pipeline, and dimensions of input data.
</p>


### Pilot Jobs
<p>
A pilot job is a design approach in batch computing, where a set of related long running batch jobs process their associated units of work. Instead of say, an array short/long running jobs fired independently. Together the two approaches act as a means of orchestration batch job deployments, where ETLs can be designed & tested on smaller subsets, and scaled out with TaskTide. 
</p>


## 5). What environments does TaskTide support?
<p>
Depends on what you mean by environment. TaskTide has been tested on both windows & linux operating systems, as well as containerized & HPC platforms.
</p>


## 6). Can I use MySQL, PostgresSQL, Microsoft SQL Server, Oracle?
<p>
Yup, <a href="/tasktide/tasktide/README.md#ii-relational-backend-configurations">see here</a> and <a href="/Database-Driver-Installation.md#1-install-required-relational-database-driver">here</a> for configuration guide.
</p>


## 7). Can I use NoSQL Databases like MongoDB, CouchDB, Oracle?
<p>
Yup, <a href="/tasktide/tasktide/README.md#i-nosql-backend-configurations">see here</a> and <a href="/Database-Driver-Installation.md#2-using-pre-packaged-nosql-database-driver">here</a> for configuration guide.
</p>


## 8). How do I use TaskTide?
<p>
TaskTide is designed as configurable command-line client where its <a href="/tasktide/tasktide/README.md#c-manager-client-configurations">Manager Client</a> for task orchestration, and its <a href="/tasktide/tasktide/README.md#b-engine-client-configurations">Engine Client</a> can used for task processing. See <a href="/FAQ.md#1-how-do-install-tasktide">this link</a> for installation. Templates will be provided to demonstrate TaskTide deployment.
</p>