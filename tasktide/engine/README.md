# TaskTide - EngineLib
<p>
Provides the transactional processing logic over <a href="/tasktide/core/README.md#1-tasktide-entities">TaskTide Entities</a>. The operating principle of the TaskTide Engine is to process ToDo work in an OS process. Engine processing overseen by an "<i>Observer Chain</i>" which implements validation logic of task processing over key life-cycle transistions of each individual "<i>WorkItem</i>", and "<i>ItemTask</i>", and feeding relevant updates back to configured database. 
</p>


## 1). TaskTide Engine Worker
<p>

</p>


## 2). TaskTide Engine Observer Chain
<p>
Separate observer chains validate task processing over their life-cycles for WorkItem, and ItemTask. Since the engine's execution chain needs only a true/false flag for pre, during, and post task processing, a lower "<i>WorkerObserver</i>" interface returning "<i>ObserverResult</i>" for the subsribed "<i>onTaskStart, onTaskProcessing, and onTaskEnd</i>" methods. Serves to separate the nuances of the abstract "<i>TimeKeeperObserver, StateObserver, and ExecutorObserver</i>" interfaces, from their utilisation in the engine's execution chain, as well as allowing them to be independently configurable, and new observers to be developed, without requiring changes to be made to utilitising class because it only depends on their boolean validation logic and placement into the chain.

The abstract "<i>StateObserver</i>" decorates the pre, during, and post task processing validation interface by commiting WorkItem/ItemTask state and corresponding timestamps to the "<a href="/tasktide/core/README.md#3-tasktide-service--manager">TaskTideService</a>" as well as the task state logic which prevents parallel threads/processes from working on the same WorkItem/ItemTask. The "<i>WorkItemExecutorObserver</i>" babysits its workload processing over its lifecycle. The optional "<i>TimeKeeper</i>" tracks execution times of WorkItems, and ItemTasks, and can be used to validate whether or not there is on average enough configured wall-time to process the next task if known (defined relative to WorkItem, ItemTask level). A setting for environments with a max wall time for jobs, and CPU time budgets.
</p>


## 3). Log Handling
<p>
It is recommended that tasks are provided as end-user developed ETL scripts sink their logs to a file, and handled separately. This is so that end-users can gauarntee where these task logs are stored, for instance with linux stdout/stderr could be sent to null device if no interested. Or a file, and pushed to S3/AzureBlob, CloudWatch/Azure Monitor. Otherwise these data are stored in an ItemTask property if less than 1MB, or into a zip if greater 1MB and acknowledged on the ItemTask (ie either has the logs, or their full file path). These are logged outputs from ETL script/executable provided, not sub-scripts/programs used by that script. In time, destinations maybe configured but this not a current priority.
</p>


## EngineLib Architecture
<p align="center">
  <img src="/tasktide/docs/uml/engine-lib-uml.svg" alt="EngineLib-UML"/>
</p>
