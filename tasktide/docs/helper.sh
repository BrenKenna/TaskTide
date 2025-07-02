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
docker container run -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=password --name 'couchDB' -p 59484:5984 couchdb:latest


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
docker container run --name cassandra -p 7000:7000 cassandra:latest
