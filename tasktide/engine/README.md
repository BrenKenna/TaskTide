# TaskTide - EngineLib
<p>
Provides the transactional processing logic over <a href="/tasktide/core/README.md#1-tasktide-entities">TaskTide Entities</a>. The operating princple of the TaskTide Engine is that only work marked as ToDo, are fetched for processing. The process is observed by an "<i>Observer Interface</i>" which handles the validation logic of task processing over the life-cycle of each individual "<i>WorkItem</i>", and "<i>ItemTask</i>". Task processing is handled by the "<i>TaskTide Processor</i>", and "<i>TaskTide Executor</i>" interfaces which decouples task tracking and parallel processing, from the actual OS process that task are executed in.
</p>


## 1). TaskTide Engine Execution Chain
<p>
The "<i>WorkUnit</i>" marker interface implicity aggregates the abstract engine components "<i>TaskTideProcessor, TaskTideExecutor, and TaskTideEngineObserver Chain</i>". The "<i>TaskTideProcessor</i>" is implemented by the separate "<i>WorkItemProcessor</i>", and "<i>ItemTaskProcessor</i>" to submit these units of work for processing in their own thread pools. A static package private "<i>FutureTrackers</i>" container was designed so that the units of work can be tracked over their life-cycle either on aggregate, on individual basici via the "<i>ExecutorServiceTracker</i>" which caches the TaskTideModel and its future in separate concurrent maps (ie WorkItems cached separatetly from ItemTasks).

Like the processor, the "<i>TaskTideExecutor</i>" is implemented by separate "<i>WorkItemExecutor</i>", and <i>ItemTaskExecutor</i>" which perform the execution operation. For WorkItems, this is "<i>ItemTaskProcessor</i>" delegation, and ItemTasks is running the associated task script attribute in an OS process. 
</p>


## 2). TaskTide Engine Observer Chain
<p>
Separate observer chains validate task processing over their life-cycles for WorkItem, and ItemTask. Since the engine's execution chain needs only a true/false flag for pre, during, and post task processing, a lower "<i>WorkerObserver</i>" interface returning "<i>ObserverResult</i>" for the subsribed "<i>onTaskStart, onTaskProcessing, and onTaskEnd</i>" methods. Serves to decouple the nuances of the abstract "<i>TimeKeeperObserver, StateObserver, and ExecutorObserver</i>" interfaces, from their utilisation from the engine's execution chain, as well as allowing them to be independently configurable, and new observers to be developed, without requiring changes to be made to utilitising class because it only depends on their boolean validation logic.

The abstract "<i>StateObserver</i>" decorates the pre, during, and post task processing validation interface by commiting WorkItem/ItemTask state and corresponding timestamp to the "<a href="/tasktide/core/README.md#3-tasktide-service--manager">TaskTideService</a>" as well as the task state logic which prevents parallel threads/processes from working on the same WorkItem/ItemTask. The "<i>WorkItemExecutorObserver</i>" babysits monitors its workload processing over its lifecycle. The optional "<i>TimeKeeper</i>" tracks execution times of WorkItems, and ItemTasks, and can be used to validate whether or not there is on average enough configured wall-time to process the next task (defined relative to WorkItem, ItemTask level).
</p>


## EngineLib Architecture
<p align="center">
  <img src="/tasktide/docs/uml/engine-lib-uml.svg" alt="EngineLib-UML"/>
</p>
