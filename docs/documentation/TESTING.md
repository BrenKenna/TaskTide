# Testing TaskTide

TaskTide uses a layered test taxonomy to exercise the project at different levels.

The test suite is organised around:

- Unit tests — test individual components and their behaviour in isolation.
- Integration tests — exercise interactions between TaskTide components and external infrastructure, including configured database backends.
- System tests — exercise TaskTide as a running system, covering behaviour across the application boundary.

The different levels complement one another: unit tests provide focused feedback, while integration and system tests exercise the interactions and runtime behaviour that cannot be fully represented by isolated tests.


## Test Infrastructure

Integration and system tests may require supporting infrastructure such as database services.

Where required, this infrastructure is provided through a test-sidecar Docker container. The sidecar exists to provide the external services required by the tests; it is not part of the TaskTide runtime.

Docker should therefore be available when running test suites that require the test-sidecar.

Running Tests

The Gradle wrapper is included with the project, so the test suite can be run without requiring a separate Gradle installation.

Running the full test suite is not recommended and will break because of dependency requirements, and intentional retention of experimental tests.
As well as TaskTide being a multi-module system package, individual test cases can create environmental conflicts. With these an annotation based
approach is used for conducting production grade testing. But the below scheme remains open to support developmental tests within IDEs like NetBeans.

```bash
./gradlew test
```


## Running TaskTide Tests

TaskTide is organised as a multi-module Gradle project. A module's tests can be run directly:

```bash
./gradlew :<core | engine | api >:<unit-tests | integration-tests | system-tests>
```

For example the below runs the unit-tests for the parser library.

```bash
./gradlew :parser:unit-tests
```


### Run a specific test class

Gradle's test filtering can be used when working on a particular test:

```bash
./gradlew test --tests "fully.qualified.TestClassName"
```

For a specific module:

```bash
./gradlew :< LIBRARY >:< TEST > \
  --tests "fully.qualified.TestClassName"
```

### Run a specific test method

```bash
./gradlew test \
  --tests "fully.qualified.TestClassName.testMethodName"
```

## Integration and System Tests

Integration and system tests require side-car databases to be provisioned. So it is recommended to [TaskTide CI workflow](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/_library.yml)

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


## CI

[TaskTide's CI](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/ci.yml) configuration runs the project's automated tests as part of the normal development workflow.

The local Gradle commands above are intended to provide the same basic entry point for running the test suite during development.

When a test depends on Docker-backed infrastructure, the CI environment provides the corresponding test-sidecar services before those tests are executed.
As shown for [library tests](https://github.com/BrenKenna/TaskTide/blob/main/.github/workflows/_library.yml).