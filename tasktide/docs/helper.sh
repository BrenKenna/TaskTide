#!/bin/bash


#############################################################
#############################################################
# 
# 1). Setup Backend for Testing
# 
#############################################################
#############################################################

#############################################
#############################################
# 
# a). Document Databases
#
#  => Mongo storates
#  => Other two have their prop as a null
# 
#############################################
#############################################

# Run image
docker container run  -p 27017:27017 mongo:latest


# Run image
docker container run -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=password -p 5984:5984 couchdb:latest
curl -X PUT http://admin:password@localhost:5984/tasktide_database

curl -X GET http://admin:password@localhost:5984/test_workflow


# Run image
docker run -d -p 8529:8529 -e ARANGO_RANDOM_ROOT_PASSWORD=password --name arangodb-instance arangodb



####################################
####################################
# 
# b). Cassandra
# 
####################################
####################################


# Run image
docker container run -e CASSANDRA_PASSWORD_SEEDER=yes -e CASSANDRA_USER=admin -e CASSANDRA_PASSWORD=password -p 9042:9042 cassandra:latest



####################################
####################################
# 
# c). Key-Value
# 
####################################
####################################

# Redis
docker container run -p 6379:6379 redis:latest

