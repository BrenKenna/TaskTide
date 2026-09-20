# TaskTide - ItemStore

The purpose of this Library is to offer support for embedded databases like SQLite, and RocksDB through a unified interface tailored for specifically for TaskTide.

The library relies on the [Mutex Lib](/tasktide/mutex) for a de-centralized read/write queue. So that distinct instances of the TaskTide engine running across distributed compute resources of HPC/server. Can operate concurrently without corrupting their shared database file on an NFS mount, without simultaneous write collisions.
