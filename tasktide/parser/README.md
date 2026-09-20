# TaskTide - Parser Lib

Uses an in-memory non-persistent GenericTree of ArgumentMaps for to represent and parse command-line arguments.

An ArgumentMap represents a collection of Arguments for a command-line operation. The GenericTree is the data structure to hold these data.

The library defines extensbile AbstractConfig class for holding configuration attributes and their initialization from configuration sources (ex microprofile-config.properties etc).
