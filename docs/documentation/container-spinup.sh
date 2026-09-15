
# Starts MariaDB & couchDB containers for test
docker container run --rm -e MARIADB_ROOT_PASSWORD=password -e MARIADB_DATABASE=tasktide -p 3306:3306 mariadb:11

docker container run --rm \
    --name mariadb \
    --detach \
    -e MARIADB_ROOT_PASSWORD=password \
    -e MARIADB_DATABASE=tasktide \
    -p 3306:3306 \
    mariadb:11

docker container run --rm \
    --name couchdb \
    --detach \
    -e COUCHDB_USER=admin \
    -e COUCHDB_PASSWORD=password \
    -p 5984:5984 \
    couchdb:3.5

docker container run --rm -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=password -p 5984:5984 couchdb:3.5

curl -u "admin:password" -X PUT http://localhost:5984/tasktide_database