# TaskTide-Mutex Lib

Generic library for a de-centralized file orientated mutex, across distributed compute resources using a shared storage resource (shown below). The library requires that the storage resource is mounted onto each compute resource participant (ex NFS).
  
The library models each mutex request as a ballot for leader-election across distributed compute resources, using a shared storage resource for persistence. Once leadership is determined by the epoch time of the unique ballot. The leader fetches an OS file lock acquired on the target. The leader-election is on JVM level are synchronised. Meaning that ballots from multiple threads on single JVM are serialised, but can occur concurrently on a host.

Precise ordering of read-writes is not as important having them just queued. While ballots are enqueued by their epoch time, and removed once completed, non-leaders observe the head of the queue based on a configurable stale file threshold. Which keeps the queue in a flux, but may not guarantee canonical ordering.

Bucketing of this algorithm has been templated to be explored further for future versions. In additon to an optional local FileChannel FileLock to serialised ballots across JVMs on the same host, both to reduce the number of simultaneous ballots.

The library was developed for the [ItemStore](/tasktide/itemstore/README.md) databases that do not require a process, or network connection. So that a de-centralized read-write queue can be used for ItemStore-Repository. That allows for multiple jobs running across the distinct hosts of a HPC, to coordinate their access patterns against the target file. <em>Without introducing the maintenence, fault-tolerance, stability, and availability requirements in deploying an additional side-car process to orchestrate the queue</em>.

<br>

<p align="center">
  <img src="/docs/assets/mutex-workflow.png" alt=""/>
</p>
