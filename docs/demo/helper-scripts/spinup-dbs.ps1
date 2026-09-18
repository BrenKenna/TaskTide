
docker container run `
    -e MARIADB_USER=admin `
    -e MARIADB_PASSWORD=password `
    -e MARIADB_ROOT_PASSWORD=rootpass `
    -e MARIADB_DATABASE=tasktide_database `
    -p 3306:3306 `
    mariadb:latest


docker container run `
    -e COUCHDB_USER=admin `
    -e COUCHDB_PASSWORD=password `
    -p 5984:5984 `
    couchdb:latest
