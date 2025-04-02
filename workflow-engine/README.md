# Workflow-Engine
Modularized look at how to develop an engine which processes the work associated with list of tasks (i.e other programs, scripts etc). An "***Engine***" should ideally run in the background, so that an interface can give tasks to perform. That interface/main can then be used to manage & monitor these tasks.

While definitely unfinished, I was most interested in the conceptualizing the above problem. For instance my main questions were:

1). How do you run OS commands in Java & what info can you get from that?

2). How do you go about doing multiple things at the same time, have the main process observe this, then make decisions on what to do? Terminate if wall time exceeded, gracefully end if resource consumption is becoming too high. Poll states, and log them to somewhere an end-user can flick through etc.

3). How do you control waiting/not waiting for them to complete?

There were also some questions about "*doing certain things in a more maintainable way*", which was nice to explore in my own time (but detailed read). These were mostly raised from college projects exploring design patterns like a "*Strategic Factory Chain*" which constructs an object given a specific type of "*Request*". The concept here also helps developing a better binary search "***Index Interface***" from an old project, because I can make better use of modelling a list of Key-Value pairs to construct an object. To address its "*bulkyness*" around searching a collection of sorted primitive types (String, Int etc).

The documentation on classes, and testing is quite lacking. As the "main" is the test module to a degree lol, but this was mostly a conceptual side-project. To touchbase with parallel computing in Java tbh.

# Core Model Classes
|**Name**|**Purpose**|
|--|--|
|**WorkItem**  |Model object which holds the unit of work, process logs, etc
|**ItemTask**|OS process to run, ex ping host
|**TaskLogging**|Holds the logs from running the OS process
|**WorkItemCollection**|Holds a collection of WorkItems
|**WorkFlow**|Holds Map of an ItemState-Enum key, and WorkItemCollection


# Core Controller Classes

|**Name**|**Purpose**|
|--|--|
|**Engine**|Uses supporting modules to process the workload given to it|
|**Runner**|Iteratively validates whether a task can be processed, ex only todo workitems can be processed|
|**Executor**|Executes a workitems task|
|**Modifier**|Handles the logic of editing WorkItem States|
|**PoolManager**|Holds worker pools for the engine, and processing of workitems and their tasks. Meaning three pools|


# Core Object Constructing Classes
The idea is that a factory is given a request form, which contains the data it needs to construct the required model object. But it means a "core" model object, needs its own concrete factory, and concrete request form.

|**Name**|**Purpose**|
|--|--|
|**ModelFactory**|Chains the ItemTask and WorkItem Factories into one interface. Which constructs an "***Object***" from a model "***Request***" form, for a specific "***ModelType***" (ie WorkItemTask)|
|**Concrete Factories**|Each concrete model has a factory which parses its request form, to construct the model desired class|
|**RequestInterface**|Is a map of a key (ie name of constructor attribute), and a value (ie value for that attribute). Each model object has its own request form.
