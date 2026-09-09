Testing TaskTide

TaskTide uses a layered test taxonomy to exercise the project at different levels.

The test suite is organised around:

- Unit tests — test individual components and their behaviour in isolation.
- Integration tests — exercise interactions between TaskTide components and external infrastructure, including configured database backends.
- System tests — exercise TaskTide as a running system, covering behaviour across the application boundary.

The different levels complement one another: unit tests provide focused feedback, while integration and system tests exercise the interactions and runtime behaviour that cannot be fully represented by isolated tests.

Test Infrastructure

Integration and system tests may require supporting infrastructure such as database services.

Where required, this infrastructure is provided through a test-sidecar Docker container. The sidecar exists to provide the external services required by the tests; it is not part of the TaskTide runtime.

Docker should therefore be available when running test suites that require the test-sidecar.

Running Tests

The Gradle wrapper is included with the project, so the test suite can be run without requiring a separate Gradle installation.

Run the full test suite

./gradlew test

This runs the project's configured tests through Gradle.

Run tests for a specific module

TaskTide is organised as a multi-module Gradle project. A module's tests can be run directly:

./gradlew :tasktide:<module>:test

For example:

./gradlew :tasktide:core:test

Run a specific test class

Gradle's test filtering can be used when working on a particular test:

./gradlew test --tests "fully.qualified.TestClassName"

For a specific module:

./gradlew :tasktide:<module>:test \
  --tests "fully.qualified.TestClassName"

Run a specific test method

./gradlew test \
  --tests "fully.qualified.TestClassName.testMethodName"

Integration and System Tests

Integration and system tests may require Docker-backed test infrastructure.

When running these tests locally, ensure Docker is available before starting the relevant Gradle task.

The appropriate test task can be run through the Gradle wrapper in the same way as other tests:

./gradlew test

or, where a particular module or test task is provided:

./gradlew :tasktide:<module>:<test-task>

The test output from Gradle identifies the tests executed and reports failures and errors.

Test Selection

During development it can be useful to start with the smallest relevant test scope and then run the broader suite before completing a change.

For example:

# Focused test
./gradlew test --tests "fully.qualified.TestClassName"

# Module tests
./gradlew :tasktide:<module>:test

# Full test suite
./gradlew test

This allows changes to be checked progressively across the unit, integration, and system test levels.

CI

The CI configuration runs the project's automated tests as part of the normal development workflow.

The local Gradle commands above are intended to provide the same basic entry point for running the test suite during development.

When a test depends on Docker-backed infrastructure, the CI environment provides the corresponding test-sidecar services before those tests are executed.
