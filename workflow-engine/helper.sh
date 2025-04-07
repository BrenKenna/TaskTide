#!/bin/bash


#############################################################
#############################################################
# 
# 1). Run Testing Databases
# 
#############################################################
#############################################################

###############################
###############################
# 
# a). MongoDB
# 
###############################
###############################

# Run test MongoDB instance
docker run -p 27017:27017 mongo


# Check connection
test-netConnection -Port 27017 -ComputerName localhost

"""
ComputerName     : localhost
RemoteAddress    : ::1
RemotePort       : 27017
InterfaceAlias   : Loopback Pseudo-Interface 1
SourceAddress    : ::1
TcpTestSucceeded : True
"""


###############################
###############################
# 
# b). DynamoDB
# 
###############################
###############################

# Run test dynamodb
docker run -p 8000:8000 amazon/dynamodb-local


# Check connection
test-netConnection -Port 8000 -ComputerName localhost

"""
ComputerName     : localhost
RemoteAddress    : ::1
RemotePort       : 8000
InterfaceAlias   : Loopback Pseudo-Interface 1
SourceAddress    : ::1
TcpTestSucceeded : True
"""