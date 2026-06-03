# TaskTide: Backend-agnostic workflow orchestration engine

## Summary
<p id="summary-1">
TaskTide is a backend agnostic workflow orchestration engine developed to bridge the Orchestration Layer gap between customers Data Application Layer development, and their scalable deployment across heterogeneous Infrastructure Layer. In modern big data platforms, workflows are defined by users but executed on distributed compute platforms. This intrinsic separation of concerns results in workflow orchestration falling outside the responsibility of either layer.
</p>

<p id="summary-2">
TaskTide enables its users to register, query, and modify their workflow deployments in real-time, while they are actively running on distributed compute resources. Demonstrated here with use-cases spanning multiple research domains, TaskTide provides a lightweight and flexible solution for scalable workflow orchestration of data-intensive applications without coupling them to specific infrastructure backends.
</p>

---

## Statement of Need
<p id="son-1">
The growth of data-intensive research and AI-driven applications increases the demand for workflow orchestration across heterogeneous computing environments (Hop et al., 2024; K. P. Kenna et al., 2016; NHLBI, 2016; Nicolas et al., 2018). Although technologies like Hadoop, SLURM, gLite are the compute scheduling bedrock for modern big data platforms adopted by service providers as their multi-tenant Infrastructure Layer System, ILS (Giri & Sharma, 2022; Salloum et al., 2016; Yoo et al., 2003). The need for Workflow Orchestration System, WOS, arises from the separate concerns of ILS offered by service providers, and Data Application Layer Software, DAS, developed by end-users.
</p>

<p id="son-2">
In this context ILS are responsible for their fault-tolerance, resource provisioning, and the execution of scheduled compute jobs. While DAS are developed by end-users to implement domain specific business logic or research workflows. These separate concerns means that the orchestration of data applications across ILS is not owned by either side. Creating a gap between intent and its billable distributed execution. 
</p>

<p id="son-3">
The caveat of the OLS gap is that there is no standardized linkage of a given data application to its scale out across distributed compute systems. Leaving the burden of relating job environment, and data application instance associations, and ensuring their correct execution context falls to the end-user to develop. Typically addressed bespoke by infrastructure-specific solutions that are verbose and tightly coupled to execution context. Thereby reducing the portability of Data Application Layer solutions, contradicting the sentiments of FAIR-aligned workflow design.
</p>

<p id="son-4">
The OLS gap introduces operational overhead for both end-users and service providers. For the end-users this comes in as development overhead for each workflow. For service providers the OLS gap can blur the boundary between infrastructure support responsibilities, and workflow issues. Collectively complicating user onboarding and support scope. Problems which are amplified by the rapid evolution of both application and infrastructure technologies, where tightly coupled solutions inherit platform-specific obsolescence risks. The complex dynamics of DALS and ILS highlight the need for flexible, backend agnostic workflow orchestration system. Where task scheduling is decoupled from task execution, and the compute infrastructure it is deployed across.
</p>

<p id="son-5">
TaskTide is shown here to contribute a fault tolerant distributed workflow orchestration system for scaling DALS, across heterogeneous ILS (HPC, and cloud). It enables its users to register, query, and modify workflow deployments in real-time, while they are executed across distributed compute environments. TaskTide is designed to operate across both relational database management systems (ex Postgres, Oracle, Maria etc), and NoSQL database systems (ex MongoDB, CouchDB, Redis etc) reflecting its backend-agnostic design.
</p>

---

## Software Design

### a). Introduction
<p id="software-design-intro">
TaskTide is a backend-agnostic workflow orchestration system designed to bridge the gap between end-user DALS development and its scalable deployment across heterogeneous ILS (Fig 1). It achieves this through decoupling task scheduling from its execution on Infrastructure Layer, and maintaining associations between workflows and their execution context (Fig 2-3).
Existing workflow systems (e.g., Snakemake, Cromwell) couple orchestration with workflow definition, while distributed computing platforms (e.g., DIRAC, PiCaS) integrate orchestration within broader infrastructure stacks. TaskTide instead isolates orchestration as a standalone, backend-agnostic layer between application and infrastructure concerns.
The following describes how TaskTide’s architecture and operational model map to practical values for end-users and service providers as a viable OLS solution (Table-1).
</p>

---

### b). TaskTide Data Model
<p id="software-design-data-model">
TaskTide defines a standardized data model that enables reproducible and traceable DALS deployments across heterogeneous ILS (ie HPC, VM, containerized). The model was motivated by the Extraction, Transformation, and Loading pattern (Gropp et al., 1996; Singh, 2022; Venkateswarlu & Vasista, 2023). Where a Workflow is a collection of distinct but related operations to be performed (i.e ETL sequence). Each distinct operation of a given Workflow is a Step, and the collection variable inputs for a given Step is a WorkItem. This structure allows workflows to be expressed as scalable units of work.
</p>

<p id="software-design-data-model-2">
To support task tracking and monitoring, metadata attributes such as WorkItem state, execution start/end time, standard out/error logs etc are also captured within the data model. In addition to a jobs execution context via metadata (type, job Id, array index etc), and host parameters & metrics (ex hostname, java version, CPU utilization etc). The mappings serve to establish direct linkages between application intent and its execution. Enabling workflow introspection, monitoring, and reproducibility across ILS. The data model is extensible, allowing users to annotate datapoints with additional metadata relevant to operational or domain-specific requirements or identifiers.
</p>

---

### c). TaskTide Execution Model
<p id="software-design-execution-model">
TaskTide coordinates distributed workflow execution via a database-backed producer-consumer pattern, enabling  scalable and fault-tolerant DALS deployments across ILS (Fig 2-3). It distinguishes itself from similar solutions through its backend agnosticism (B. Kenna, 2025; SURF, 2025). Where multiple backend database technologies are supported, spanning both relational and non-relational SQL semantics (Fig 2-3). The operational value of this backend agnosticism feature is that it allows TaskTide to be run on different Infrastructure Layer solutions (Fig-1). Expanding the service provider space it can be provisioned into (HPC, Grid, Cloud), and brings portability to its user bases DALS, over bespoke solutions per Infrastructure Layer technology.
</p>

<p id="software-design-execution-model-2">
In this model, workers fetch available WorkItems from a centralized data store, rather than being explicitly assigned tasks (early task binding semantics also supported). The consumer-based execution model allows for horizontal scaling across compute resources and supports dynamic workload distribution without coupling workflows to specific schedulers. For DALS deployment, this means that failures can be recovered by simply resetting/reassigning incomplete work. Enabling fault-tolerance and ensure workflows progress reliably even in the presence of ILS failures.
</p>


<p id="software-design-execution-model-3">
These behaviours are implemented to through the Tasktide-Engine, where each EngineWorker instance consumes available tasks for a workflow, and an EngineObserverChain feeds back task updates to the configured backend as tasks progress through their lifecycle (Fig-4).
</p>

---

### d). System Interfaces
<p id="software-design-system-interfaces">
TaskTide exposes orchestration capabilities through programmatic command-line and web interfaces (Table 2-3). These interfaces allow Tasktide to be deployed as a service within distributed architectures, supporting integrations with external systems, web-based dashboards, and multi-tenant environments. By externalizing orchestration control, TaskTide enables end-user workflows to remain decoupled from the ILS they are deployed across.
</p>

<p id="software-design-system-interfaces-2">
The TaskTide Manager API provides configurable mechanisms for registering, querying, and modifying workflows while they are actively running. This enables both automated and interactive control of distributed workflow deployments. Supporting use-cases from batch processing adaptive, long-running computations. The TaskTide Engine API provides configurable mechanisms for processing targeted workflows. Such as validation leniencies, multi-threading, and policy driven execution policies. Execution policies defining specifics for workflow traversals, such as batch or service orientated for automating producer-consumer pipelining.
</p>

---

### e). Deployment
<p id="software-design-deployment">
TaskTide can be deployed for arbitrary CRUD operations against backend database, engine instances across server farm or as web service. This flexibility in workflow orchestration is achieved through TaskTide’s modular design (ie Manager CQRS from Core Lib, Engine from Engine Lib) and unified client which is configured to run required component.
</p>

<p id="software-design-deployment-2">
Externalizing TaskTide’s configuration to command-line, and an application properties file allows TaskTide to be deployed into different environments (HPC, Cloud, Containerized). As well as providing the flexibility to configure additional parameters per instance such as the execution policy of a given engine or enqueuing the next processing step for a task during engine execution. Enabling programmatic automation, in addition to ad hoc and interactive usage.
</p>

<p id="software-design-deployment-3">
TaskTide’s unified client simplifies the configuration of TaskTide as a system (Table-2). As only a single configuration source is needed for configuring its components, and passing down dependency configurations directly down those libraries such as Jakarta-NoSQL, Hibernate, and Jetty. Taken with backend-agnosticism these features support the deployment of TaskTide across wider range of service provider environments. For the end-users on DALS side, reduces the need for bespoke orchestration solutions tied to a specific platform.
</p>

---

### f). Relation to Pilot Job Abstraction
<p id="software-design-pj-absraction">
TaskTide design was motivated by viewing ETLs as the base unit of work for the Pilot Job abstraction (Fig-2, Fig-5). Coming from the production environment experience, where tracking progress, resource utilization, multi-system deployments, and decorating units of work with internal identifiers were all required (Hop et al., 2024; K. P. Kenna et al., 2016; NHLBI, 2016; Nicolas et al., 2018).
</p>

<p id="software-design-pj-absraction-2">
The advantage in combining the ETL, and Pilot Job design patterns is that their scaling inherently translates to a distributed divide and conquer approach (Singh, 2022; Turilli et al., 2019). For example, scaling parameters for an ETL include the number of tasks for a Step from practical data slicing, and coherent workflow definitions which also capture relevant data wrangling/cleaning tasks separately. For the pilot job side, scaling parameters adjust the number of tasks that each instance process or optimize resource use ideally per ETL per Workflow. These aspects also creates a communicational bridge between the customers suite of Data Application Layer software, and its scale-out onto service provider Infrastructure Layer. In addition to supporting both early and late task binding semantics.
</p>

<p id="software-design-pj-absraction-3">
In delegating pilot provisioning to the underlying job scheduler ILS, and workflow enqueuing to DALS. Enables TaskTide to be a backend-agnostic OLS deployable across heterogeneous ILS. While providing similar benefits in terms of scalability, flexibility, real-time introspection and workflow-level control through its data model.
</p>

---

## State of the Field
<p id="sof">
TaskTide was developed to address limitations in how workflow orchestration is deployed and implemented across heterogeneous computing environments. Existing workflow systems such as Snakemake and Cromwell provide mature workflow definition and execution frameworks, but tend to couple orchestration closely to workflow specification and execution semantics. Conversely, pilot-job and distributed computing frameworks such as DIRAC, PiCaS, and PyAnamo decouple workload execution from resource provisioning, providing scalable mechanisms for distributed task processing.
</p>

<p id="sof-2">
Among these, PiCaS and PyAnamo are most closely aligned with TaskTide due to their database-centred approach to workload coordination. Where task scheduling is separated from pilot provisioning. TaskTide extends this design philosophy through backend-agnostic persistence, support for both relational and NoSQL database systems, and a workflow-centric data model that explicitly captures execution context, observability, and workflow state. Unlike DAG-oriented workflow systems, TaskTide models orchestration through user-defined workflow collections, steps, and work items. Allowing execution policies and infrastructure concerns to remain independent of workflow definition.
</p>

<p id="sof-3">
As discussed throughout the Software Design section (Table 1), TaskTide positions orchestration as a standalone Orchestration Layer System (OLS) between Data Application Layer Software (DALS) and Infrastructure Layer Systems (ILS). This enables fault-tolerant, observable, and adaptable workflow deployment while avoiding tight coupling to either application-specific logic or infrastructure-specific technologies.
</p>

---

## Research Impact Statement
<p id="ris">
TaskTide was developed from workflow orchestration approaches used by the author to support large-scale biomedical and data-intensive research projects (Hop et al., 2024; K. P. Kenna et al., 2016; Nicolas et al., 2018). These production environments required scalable workflow deployment, task tracking, execution-context mapping, and coordination across heterogeneous computing infrastructures. TaskTide consolidates and generalizes these operational requirements into a reusable, backend-agnostic workflow orchestration system. By making these capabilities available through a standalone Orchestration Layer System, TaskTide broadens access to workflow orchestration practices that were previously implemented through bespoke or infrastructure-specific solutions.
</p>

---

## AI Usage Disclosure
<p id="aiud">
The design, architecture, implementation, evaluation, and scientific claims of TaskTide were developed by the author. AI tools were not used to design the TaskTide system itself, or to generate its core workflow orchestration mechanisms.
</p>

<p id="aiud-2">
ChatGPT and GitHub Copilot were used as assistive tools during manuscript preparation to improve readability, phrasing, and clarity of the text. They were also used to explore implementation patterns and software engineering practices (e.g., testing strategies, REST API testing approaches, and integration patterns such as embedded Jetty and Jersey HTTP test harnesses), which informed but did not define the design or implementation of TaskTide.
</p>

<p id="aiud-3">
ChatGPT was additionally used to generate auxiliary demonstration code for non-core use cases presented in this work. These were image analysis scripts and a Super Mario reinforcement learning workload, because they served as illustrative examples of TaskTide’s applicability beyond its primary bioinformatics-oriented motivation. These components, however, are not part of TaskTide’s core architecture or contribution.
</p>


## Figures and Tables

### Figure 1: High Level Layered Architecture

| Figure |
|--------|
| ![Figure 1](/paper/tables-figs/figure1.png) |

<p id="fig-1">
Figure 1: Shows the positioning of TaskTide as the Orchestration Layer which addresses the gap between customer Data Application Layer development, and its session-less scale-out across heterogeneous Infrastructure Layer as demonstrated by the “Use Cases” section.
</p>

---

### Figure 2: TaskTide Operating Principle

| Figure |
|--------|
| ![Figure 2](/paper/tables-figs/figure2.png) |

<p id="fig-2">
Figure 2: Shows the Producer-Consumer operating principle of the TaskTide workflow engine. 1). The TaskTide Manager command-line client is the interface where users register and query their tasks across user-defined workflows. 2). The TaskTide Repository is the ambassador interface which manages the storage/retrieval of tasks and persists its data to the configured backend database (NoSQL, or SQL). 3). The TaskTide Engine command-line client consumes the required task set, or task sets.
</p>

---

### Figure 3: TaskTide Architecture

| Figure |
|--------|
| ![Figure 3](/paper/tables-figs/figure3.png) |

<p id="fig-3">
Figure 3: Shows the architecture of the TaskTide Workflow Orchestration Engine.
</p>

---

### Figure 4: TaskTide WorkItem Lifecycle

| Figure |
|--------|
| ![Figure 4](/paper/tables-figs/figure4.png) |

<p id="fig-4">
Figure 4: Shows the lifecycle of tasks registered in TaskTide (6-a) which is enacted by the TaskTide-Engine (6-b). 1). The TaskTide-Engine fetches the configured workload from TaskTide Repository. 2). The TaskTide Engine Worker distributes available tasks across configured thread pools for parallel processing. 3). The TaskTide Engine Executor instances spawn OS processes for task execution. 4). The TaskTide Engine Observer Chain performs task validation subscriptions (onTaskStart, onTaskProcessing, onTaskEnd) as tasks transition through their lifecycle (6-a).
</p>

---

### Figure 5: Mapping TaskTide to Pilot Job Abstraction

| Figure |
|--------|
| ![Figure 5](/paper/tables-figs/figure5.png) |

<p id="fig-5">
Figure 5: Shows how TaskTide’s session less internals map to the pilot-job abstraction. 1). Pilot Provisioning is delegated to the underline platform as task tracking is more relevant for TaskTide than a given job. 2). Workload Management is implemented by the TaskTide Repository where the configured database acts as the single source of truth for task lifecycle management and where binding semantics are expressed. 3). Task Execution is performed by the TaskTide Engine client where the engine pulls available tasks from configured/arbitrary collections, executes their units of work, feeding back updates observed across the execution process in real-time (ex logs, current and completed state).
</p>

---

### Figure 6: high Level Development Approach

| Figure |
|--------|
| ![Figure 6](/paper/tables-figs/figure6.png) |

<p id="fig-6">
Figure 6: Iterative and incremental development approach of TaskTide. Each stage had key deliverables which were supported by their use-case implementation. 1). Workflow Orchestration was verified using bioinformatic pipelines. 2). Task Binding Semantics were explicitly verified using a serialized Julia function runner for early task binding, and SparkR image analysis application for late task binding. 3). Containerized Deployment was verified using an AI play time optimizer Python application.
</p>

---

### Table 1: TaskTide Feature Summary

| Table |
|--------|
| ![Table 1](/paper/tables-figs/table-1.png) |

<p id="table-1">
Table 1: Summaries the feature contributions of TaskTide’s modules and translates these features to their operational value.
</p>

---

### Table 2: TaskTide Configurations

| Table |
|--------|
| ![Table 2](/paper/tables-figs/table-2.png) |

<p id="table-2">
Table 2: Example set of configuration properties for each TaskTide client.
</p>

---

### Table 3: TaskTide API Endpoints

| Table |
|--------|
| ![Table 2](/paper/tables-figs/table-3.png) |

<p id="table-3">
Table 3: Command Query Responsibility Segregation (CQRS) design of TaskTide-Web API. The RESTful API “/service/” resources exposes CRUD, and command orientated “/manager/” resources for frontend and client operations.
</p>

---



## References






<p id="">

</p>