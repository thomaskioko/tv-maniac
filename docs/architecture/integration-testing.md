# Integration Testing

## Table of Contents

- [Module Layout](#module-layout)
- [Test Harness](#test-harness)
- [Robot Pattern](#robot-pattern)
- [Network Stubbing](#scenarios-and-network-stubbing)
- [Running Tests](#running-integration-tests)

Integration tests run the full Android UI stack under Robolectric using a real Metro dependency graph with test overrides (fake auth, mock Ktor engines, test dispatchers).

## Module Layout

- **`core/integration/infra`**: DI overrides and fakes. KMP-based.
- **`core/integration/ui`**: UI scaffolding (`IntegrationTestEnvironment`, `NetworkStubber`, `BaseRobot`).
- **`:app`**: Glue layer, flow tests, and robots.

## Test Harness

- **`TvManiacTestApplication`**: Builds the test graph with overrides.
- **`TvManiacTestActivity`**: Renders `RootScreen` using real presenters and navigation.
- **`BaseAppRobolectricTest`**: Abstract base class wiring the environment, resetting auth, and providing lazy robots.

## Robot Pattern

Each screen has a robot wrapping `ComposeContentTestRule`. Robots express intent without calling repositories or DAOs.

```kotlin
internal class DiscoverRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {
    fun verifyScreenShown() = verifyTagShown(DiscoverTestTags.SCREEN_TEST_TAG)
    fun clickShow(id: Long) = click(DiscoverTestTags.showCard(id))
}
```

Assertions target UI state visible to the robot.

## Network Stubbing

Uses `Scenarios` to group stubs by feature area. `NetworkStubber` registers responses:

- **`Fixture(path)`**: Loads JSON from `core/integration/infra/src/androidMain/resources/fixtures/` via `ClassLoader.getResourceAsStream`. Path is relative to that root (e.g. `tmdb/tv_details_1396.json`).
- **`Success(body)`**: Inline JSON with HTTP 200.
- **`Error(status, body)`**: Specific HTTP status.

`stubByQuery` allows varying responses based on query parameters. Stubs are cleared between tests.

## Writing a Flow Test

1. Add flow test class to `compose/flows/` extending `BaseAppRobolectricTest`.
2. Stub endpoints in `@Before`.
3. Use robots to express multi-step journeys.
4. Assert on UI state.

## Running Tests

```bash
./gradlew :app:test
./gradlew :app:test --tests "com.thomaskioko.tvmaniac.app.test.compose.flows.discover.DiscoverToShowDetailsFollowFlowTest"
```
