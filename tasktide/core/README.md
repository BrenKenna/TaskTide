# TaskTide - CoreLib
<p>
Provides the model entities for the <a href="/tasktide/docs/assets/tasktide-arch.png">TaskTide System</a>, and their persistence to configured backend (ie Daemonless RocksDB/SQLite, Relational, NoSQL) through type constrained repository, and service ambassador pattern. The entity classes for TaskTide are summarized below, this Model View Controller complete served to simplify the design of the "<a href="/tasktide/engine/README.md">TaskTide Engine Library</a>", "<a href="/tasktide/api">TaskTide WebAPI</a>", and "<a href="/tasktide/tasktide/README.md">TaskTide Client Appilcation</a>" views.
</p>


## 1). TaskTide Entities
<p>
TaskTide models a "<i>Workflow</i>" as a collection of "<i>Steps</i>" where each Step has a collection of work units to perform "<i>WorkItem</i>". In decoupling the units of work away from their meta-data (ie Step, Workflow), TaskTide supports both arbitrary and structured Task processing. Meaning, that not all work units to be orchestrated by TaskTide have to be explicitly defined under a Workflow-Step model.
</p>

<p>
A "<i>WorkItem</i>" is a hirearchal entity whose life-cycle is managed by TaskTide through its state "<i>ToDo, Locked, Error, Done</i>". It is composed of a "<i>Workload</i>" that is a map of work to perform "<i>ItemTask</i>", which enables TaskTide to support nested work units, if required. An "<i>ItemTask</i>" holds the work to peform as a task script string, and is composed of an embedded "<i>TaskLogging</i>" model which holds the operational data from task script execution (ie stderr/out logs, start/end time, CPU duration, execution status). 
</p>

<p>
A "<i>Workflow</i>" is an entity that models a collection of related workloads as a map of "<i>Steps</i>". A "<i>Step</i>" is an entity that relates a collection of "<i>WorkItems</i>". Storing an alias for a Step in a WorkItem decouples meta-data from core data. TaskTides manager client through which WorkItems can imported/exported from user input.
</p>


## 2). TaskTide Repository
<p>
The TaskTide repositories were modelled as generic abstract interfaces constrained to "<i>TaskTideModel</i>", to separate the concerns from backend integration (ie Jakarta-NoSQL, JPA-Relational, and ItemStore-RocksDB/SQLite) from queries against the entity collection (ie "<i>Workflow, Step, WorkItem</i>"). Where the abstract "TemplateRepository" for Jakarta-NoSQL, "JpaRepository" for Relational Databases, and "<a href="/tasktide/itemstore/README.md#tasktide---itemstore">ItemStoreRepository</a>" classes all implement the logic for "<i>Create, Read, Update, Delete</i>" operations against their backend. Allowing the concrete implementations for Workflow, Step, WorkItem to apply this logic to their related target model. Allowing utilising interfaces to rely on the abstract typed interface, and a "<i>RepositoryType</i>" to strategically construct them.
</p>


## 3). TaskTide Service & Manager
<p>
A given "<i>TaskTideService</i>" instance (Workflow, Step, WorkItem) is composed with its corresponding "<i>TaskTideRepository</i>" which decouples the repository complexity (backend database) from the implementing class' business logic into a configurable singleton "<i>TaskTideServiceManager</i>" (ie configured once and reused). Which for the TaskTide Engine Client is task processing, and TaskTide Manager Client is task orchestration. The Manager package builds on this logic providing end-user facing interfaces for task import/export through its own "<i>ManagerTask</i>" model, which performs TaskTideModel conversion.
</p>


### CoreLib Architecture
<p align="center">
  <img src="/tasktide/docs/uml/core-lib.svg" alt="CoreLib-UML"/>
</p>
