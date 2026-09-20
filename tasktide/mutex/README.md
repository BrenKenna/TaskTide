# TaskTide-Mutex Lib

Generic library for de-centralized file orientated mutex across distributed processes with an NFS mount (shown below).
  
The library models each mutex request as a ballot for leader-election across distributed compute resources. Once determined the leader fetches an OS file lock acquired on the target. Precise ordering of read-writes is not as important having them just queued by their epoch time. Bucketing of this algorithm used by has been templated to be further for future versions.

The library was developed for [ItemStore](/tasktide/itemstore/README.md) databases that do not require a process, network connection. So that a de-centralized read-write queue can be used for ItemStore-Repository. Allowing for multiple jobs running across the distinct hosts of a HPC to coordinate their access patterns against target file. <em>Without requiring the submission of an additional side-car process to coordinate for that job fleet</em>. 

<br>

<p align="center">
  <img src="/docs/assets/mutex-workflow.png" alt=""/>
</p>
