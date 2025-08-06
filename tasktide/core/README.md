# TaskTide - CoreLib
<p>
Provides the model entities for the <a href="/tasktide/docs/assets/tasktide-arch.png">TaskTide System</a>, and orchestrates the persistence to configurable backend (ie File Based-RocksDB/SQLite, Relational, NoSQL) through type constrained Service-Repository pattern. These entity classes for TaskTide are summarized below, the complete design served to simplify the design of the "<a href="/tasktide/engine/README.md">TaskTide Engine Library</a>" and "<a href="/tasktide/tasktide/README.md">TaskTide Client Appilcation</a>".
</p>


## 1). TaskTide Entities
<p>
The entities that TaskTide uses models a "<i>Workflow</i>" as a collection of "<i>Steps</i>" where each Step has a collection of work units to perform "<i>WorkItem</i>". Due to the decoupling of work units away from their meta-data (step, workflow), TaskTide supports both arbitrary and structured Task processing. Meaning, not all work units to be orchestrated by TaskTide have to be defined under a Workflow-Step model.
</p>

<p>
A WorkItem is hirearchal entity whose life-cycle is managed by TaskTide through its state "<i>ToDo, Locked, Error, Done</i>". It is composed of a "<i>Workload</i>" that is a map of work to perform "<i>ItemTask</i>", which enables TaskTide to support nested work units, if required. An "<i>ItemTask</i>" holds the work to peform as a task script string, and is composed of an embedded "<i>TaskLogging</i>" modelpA which holds the operational data from task script execution (ie stderr/out logs, start/end time, CPU duration, execution status).
</p>

<p>
The hirearchal "<i>Workflow</i>" is a stateless entity that models a collection of related workloads as a map of "<i>Steps</i>". A "<i>Step</i>" is a stateful entity that relates to "<i>WorkItems</i>" by its Id which they store. Storing an alias for a Step in a WorkItem decouples meta-data from core data.
</p>


## 2). TaskTide Repository
<p>
The repository was modelled as an abstract interface constrained to "<i>TaskTideModel</i>", was to separate the concerns from backend integration (ie Jakarta-NoSQL, JPA-Relational, and ItemStore-RocksDB/SQLite) from queries against the entities (ie "<i>Workflow, Step, WorkItem</i>"). Where the abstract "TemplateRepository" for Jakarta-NoSQL, "JpaRepository" for Relational Databases, and "<a href="/tasktide/itemstore/README.md#tasktide---itemstore">ItemStoreRepository</a>" classes all implement the logic for "<i>Create, Read, Update, Delete</i>" operations against their backend. Allowing the concrete implementations for Workflow, Step, WorkItem to apply this logic to the related target, and a "<i>RepositoryType</i>" to enable their strategic construction aided by respective utilities.
</p>


## 3). TaskTide Service & Manager
<p>
A given "<i>TaskTideService</i>" interface (Workflow, Step, WorkItem) is composed with a "<i>TaskTideRepository</i>" decouples the repository complexity (backend database) from the implementing class' business logic into a configurable singleton "<i>TaskTideServiceManager</i>" (ie configured once and reused). Which for the TaskTide Engine Client is task processing, and TaskTide Manager Client is task orchestration. The Manager package builds on this logic providing end-user facing interfaces for task import/export through its own "<i>ManagerTask</i>" model, which performs TaskTideModel conversion.
</p>


### CoreLib Architecture
<p align="center">
  <img src="/tasktide/docs/uml/core-lib.svg" alt="CoreLib-UML"/>
</p>
