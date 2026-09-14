# Testing TaskTide

TaskTide uses a traditional test taxonomy pyramid to examine the project across different levels of the test suite.

The test suite is organised around:

    - Unit tests: assess individual components and their behaviour in isolation.
    - Integration tests: evaluate interactions between TaskTide components and external infrastructure, including configured database backends.
    - System tests: examine TaskTide as a running system, covering behaviour across the application boundary or library as a functional entrypoint.
    - Acceptance tests: most informative for feature development and [production behaviours of TaskTide](https://use-cases.tasktide.org).

The different levels complement one another: unit tests focus on individual components that make up TaskTide, integration tests focus on these components interact and system tests on the interactions and runtime behaviour that cannot be fully represented by isolated tests, and acceptance tests to assess how system usability, feature prioritisation etc.

Please note ***TaskTide's test taxonomy is the formal means of testing TaskTide. The broad build test is retained for IDE compatability***.

---

## 1). Test Infrastructure

Where required, supporting infrastructure for integration and system test are provided through a test-sidecar container. The sidecar containers exists to provide the external services required by the tests; it is not part of the TaskTide runtime.

Docker/apptainer should be available when running test suites that require the test-sidecar.

---

## 2). Running Tests

The Gradle wrapper is included with the project, so the test suite can be run without requiring a separate Gradle installation.

Running the full test suite is not recommended and will break because of dependency requirements, and intentional retention of experimental tests.

Additionally TaskTide being a multi-module system package, individual test cases can create environmental conflicts. With these an annotation based
approach is used for conducting production grade testing.

The below scheme remains open to support IDEs integration.

```bash
./gradlew test
```

---

## 3). Running TaskTide Tests

TaskTide is organised as a multi-module Gradle project. A its module tests can be run directly:

```bash
./gradlew :<core | engine | api >:<unit-tests | integration-tests | system-tests>
```

For example the below runs the unit-tests for the parser library.

```bash
./gradlew :parser:unit-tests
```

---

## 4). Integration and System Tests

Integration and system tests require side-car databases to be provisioned. So it is recommended to follow [TaskTide CI workflow](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/tasktide-ci-test-library.yml)

When running these tests locally, ensure Docker is available before starting the relevant Gradle task.
The appropriate test task can be run through the Gradle wrapper in the same way as other tests.

Where a particular module or test task is provided, this is especially relevant for core, engine, api, tasktide modules.

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


## 5). CI

[TaskTide's CI](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/tasktide-ci.yml) configuration runs the project's automated test suite after assembly is verfied, as part of the normal development workflow.

The local Gradle commands above are intended to provide the same basic entry point for running the test suite during development.

When a test depends on Docker-backed infrastructure, the CI environment provides the corresponding test-sidecar services before those tests are executed.
As shown for [library tests](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/tasktide-ci-test-library.yml).
