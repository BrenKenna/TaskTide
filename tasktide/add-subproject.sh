#!/bin/bash

# Create folders 
cd TaskTide/tasktide
mkdir -p \
    parser/src/main/java parser/src/test/java \
    parser/src/main/resources parser/src/test/resources


# Add references to build and settings
cp mutex/build.gradle parser/