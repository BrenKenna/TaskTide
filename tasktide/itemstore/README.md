# TaskTide - ItemStore
<p>
The purpose of this Library is to offer support for File based Databases like SQLite, and RocksDB, locking coordination system so that different instances of TaskTide can read/write to the "backend".
</p>

<p>
|Class|Purpose  |
|--|--|
| Item  | Data class that model is converted to for storage |
| ItemStore | Interface providing CRUD logic |
| AbstractItemStore | Caches masterDB into a prototype, decorating implementation |
| RocksDBStore | Implements the AbstractItemStore |
| DbTarget | Supports Master vs Prototype actions |
</p>


<p align="center">
  <img src="/tasktide/docs/uml/itemstore-uml.svg" alt="ItemStore-UML"/>
</p>