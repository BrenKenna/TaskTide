# Testing TaskTide

TaskTide uses a traditional test taxonomy pyramid to examine the project across different levels of the test suite.

For TaskTide the test suite is organised around:

1). Unit tests assess individual components and their behaviour in isolation like JSON SerDe or parameter configuration.

2). Integration tests evaluate interactions between TaskTide components and external infrastructure. Like read/write operations against configured database via TaskTide service ambassador API.

3). System tests examine TaskTide as a running system, covering behaviour across the application boundary or library as a functional entrypoint. Like registering tasks/workflows through Web-API or processing tasks through the Engine-API.

4). Acceptance tests specifically examine human use of TaskTide, and are most informative for feature development and [production behaviours of TaskTide](https://use-cases.tasktide.org).

These different levels complement one another and together act as a platform to assess & improve runtime behaviour that cannot be fully represented by isolated tests, assess system usability, guide feature prioritisation, or gaps in feature development.

Please note ***TaskTide's test taxonomy is the formal means of testing TaskTide. The broad build test is retained for IDE compatability***.

---

## 1). Test Infrastructure

Due to the nature of TaskTide, integration and system tests require access to a database. For the purposes of this document test database container are provisioned.

---

## 2). Running Tests

The Gradle wrapper is included with the project, so the test suite can be run without requiring a separate Gradle installation.

Running the full test suite is not recommended and will break because of dependency requirements, and intentional retention of experimental tests that guided TaskTide's test driven development (developed from test code out).

Additionally since TaskTide is a multi-module system. Individual test cases can create environmental conflicts not encountered by, or representational of normal use. With these an annotation based
approach is used for conducting production grade testing.

To support development however, the traditional below scheme remains open to support IDE integration.

```bash
./gradlew test
```

---

## 3). Running TaskTide Tests

TaskTide is organised as a multi-module Gradle project. A its module tests can be run directly:

```bash
./gradlew \
    :<core | engine | api >:\
    <unit-tests | integration-tests | system-tests>
```

For example the below runs the complete test suite for the parser library.

```bash
./gradlew :parser:unit-tests
./gradlew :parser:integration-tests
./graldew :parser:system-test
```

---

## 4). Integration and System Tests

Integration and system tests require access to both NoSQL and SQL databases. So it is recommended to follow [TaskTide CI workflow](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/tasktide-ci-test-library.yml) where each are purged between tests.

When running these tests locally, ensure Docker is available before starting the relevant Gradle task.
The appropriate test task can be run through the Gradle wrapper in the same way as other tests.

```bash
# Starts MariaDB & couchDB containers for test
docker container run --rm --name mariadb --detach \
  -e MARIADB_ROOT_PASSWORD=password \
  -e MARIADB_DATABASE=tasktide \
  -p 3306:3306 mariadb:11

docker container run --rm --name couchdb --detach \
  -e COUCHDB_USER=admin \
  -e COUCHDB_PASSWORD=password \
  -p 5984:5984 couchdb:3.5

# Run test
./gradlew :< LIBRARY >:< TEST >

# Kill dependent containers
docker container kill mariadb couchdb
```


## 5). Continously Integration

[TaskTide's CI](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/tasktide-ci.yml) runs the project's automated test suite after assembly has been verfied, and then verifies that a container image can be built for it and is usable (to pre-assess continous deployment). 

The local Gradle commands above are intended to provide the same basic entry point for running the test suite during development.

When a test depends on Docker-backed infrastructure, the CI environment provides the corresponding sidecar services before those tests are executed.
As shown for [library tests](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/tasktide-ci-test-library.yml).
