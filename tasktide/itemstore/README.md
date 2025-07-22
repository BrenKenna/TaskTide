# TaskTide - ItemStore
The purpose of this Library is to offer 1). Support for File based Databases like SQLite, and RocksDB, and 2). Lock Coordination system so that different instances of TaskTide can read/write to the "backend".

Currently this is branch is only developed with RocksDB & SQLite in mind, and RocksDB is the only one implemented. However, given an Item is stored, these will be implemented to cover both SQLite, and SQL backends.

|Class|Purpose  |
|--|--|
| Item  | Data class that model is converted to for storage |
| ItemStore | Interface providing CRUD logic |
| AbstractItemStore | Caches masterDB into a prototype, decorating implementation |
| RocksDBStore | Implements the AbstractItemStore |
| DbTarget | Supports Master vs Prototype actions |


<p align="center">
  <img src="itemstore-uml.svg" alt="ItemStore-UML" width="300"/>
</p>