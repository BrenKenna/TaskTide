#!/bin/bash


##################################
##################################
##
## i). couchDB
##
##################################
##################################

# Run image
docker container run -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=password -p 5984:5984 couchdb:latest

curl -X PUT http://admin:password@localhost:5984/tasktide_database | jq '.'

curl -X GET http://admin:password@localhost:5984/_all_dbs | jq '.'

curl -X GET http://admin:password@localhost:5984/tasktide_database/_all_docs | jq '.'



# Create indexes
jq -r 'keys[]' ./indexes.json | while read name
do
    echo "Indexing:\\t$name"
    fields=$(jq -c --arg n "$name" '.[$n]' ./indexes.json | jq .fields)
    curl -u "admin:password" -v -H "Content-Type: application/json" \
        -X POST http://couchdb:5984/tasktide_database/_index \
        -d "$(jq -n \
            --arg name "$name" \
            --argjson fields "$(jq -c --arg n "$name" '.[$n].fields' ./indexes.json)" \
            '{
                index: { fields: $fields },
                name: $name,
                type: "json"
            }')" | jq '.'
    sleep 1
done


##################################
##################################
##
## ii). mongoDB
##
##################################
##################################


# Run image
docker container run  -p 27017:27017 mongo:latest


```{mongosh}
use tasktide_database

// WorkItem Step indexes
db.WorkItem.createIndex({ StepName: 1 }, { name: "workItemStepIndex" })
db.WorkItem.createIndex({ StepId: 1 }, { name: "workItemStepIdIndex" })
db.WorkItem.createIndex({ ItemState: 1 }, { name: "workItemStateIndex" })
db.WorkItem.createIndex({ StepName: 1, ItemState: 1 }, { name: "workItemStepNameStateIndex" })
db.WorkItem.createIndex({ StepId: 1, ItemState: 1 }, { name: "workItemStepIdStateIndex" })
db.WorkItem.createIndex({ JobEnvironmentId: 1 }, { name: "entityJobEnvIndex" })
db.WorkItem.createIndex({ StepName: 1, JobEnvironmentId: 1 }, { name: "workItemStepNameJobEnvIdIndex" })
db.WorkItem.createIndex({ StepId: 1, JobEnvironmentId: 1 }, { name: "workItemStepIdJobEnvIdIndex" })

// Workflow indexes
db.Workflow.createIndex({ WorkflowName: 1 }, { name: "workflowNameIndex" })

// Step indexes
db.Step.createIndex({ StepName: 1 }, { name: "stepNameIndex" })
db.Step.createIndex({ StepState: 1 }, { name: "stepStateIndex" })
db.Step.createIndex({ StepName: 1, StepState: 1 }, { name: "stepNameStateIndex" })
db.Step.createIndex({ WorkflowId: 1 }, { name: "workflowIdIndex" })

```