# TV Maniac Agent Rules

## Project Overview
TV Maniac is a Kotlin Multiplatform (KMP) project for tracking TV shows. It follows a highly modularized Clean Architecture with a strict separation of concerns.

## Architecture & Tech Stack
- **KMP**: Shared business logic, state management, and data layer.
- **Clean Architecture**: Organized into `data`, `domain`, `presenter`, and `ui` layers.
- **Metro**: A custom compile-time Dependency Injection (DI) system.
- **Decompose**: Used for shared navigation and lifecycle management.
- **Store 5**: Used for data fetching (Fetcher) and caching (SourceOfTruth).
- **SQLDelight**: Local persistence.
- **UI**: Jetpack Compose for Android, SwiftUI for iOS.

## Core Mandates & Conventions

### 1. Modularization & Dependencies
- **Strict API/Implementation Split**: Most modules are split into `:api` and `:implementation` (or implicit).
- **Rule**: Modules MUST depend on `api` modules of other features, never `implementation` (except for entry points like `:app` and `:ios-framework`).
- **Feature Structure**: Features follow a 3-module split:
  - `nav`: Contains routes, parameters, and navigation-related DI.
  - `presenter`: Contains business logic, state management (MVI), and domain interactors.
  - `ui`: Contains platform-specific UI (Compose).

### 2. Dependency Injection (Metro)
- Use `@DependencyGraph`, `@GraphExtension`, and `@BindingContainer` for DI.
- **Naming**:
  - DI interfaces MUST use the `*Graph` suffix.
  - Binding providers MUST use the `*BindingContainer` suffix.
- **Scopes**:
  - `AppScope`: Singleton/Application lifetime.
  - `ActivityScope`: Activity lifetime.
  - `ScreenScope`: Decompose component lifetime.

### 3. Navigation (Decompose)
- Use `NavRoute` for standard navigation and `SheetConfig` for bottom sheets.
- **Annotations**:
  - `@NavScreen`: Annotate presenters for standard screens.
  - `@NavSheet`: Annotate presenters for bottom sheets.
  - `@TabScreen`: Annotate presenters for tab screens.
- **Codegen**: Navigation is largely handled via codegen based on these annotations.

### 4. Presentation Layer (MVI)
- **Presenters**:
  - MUST use `@AssistedInject` and have an `@AssistedFactory`.
  - MUST extend `ComponentContext` (via delegation).
  - MUST expose state as `Value<State>` (for Decompose) or `StateFlow<State>`.
  - MUST use a `dispatch(Action)` function for UI events.
- **Naming**: `*Presenter`, `*Action`, `*Content` (for state).
- **Standardized Helpers**:
  - Use `ObservableLoadingCounter` for tracking loading states.
  - Use `UiMessageManager` for managing transient UI messages (errors, snackbars).
  - Use `.collectStatus()` extension to handle flow statuses and pipe errors/loading to the above managers.

### 5. Data Layer (Store)
- Use `Store 5` for fetching and caching.
- `Fetcher`: For network requests (Ktor).
- `SourceOfTruth`: For local caching (SQLDelight).
- `Validator`: To determine if cached data is still valid.

### 6. Localization
- **Rule**: Never use hardcoded strings or platform-specific string resources in shared code.
- Use `Localizer` interface for shared string resolution.
- Use `MR` (MOKO Resources) for string and plural definitions.
- In Compose UI, use `resource.resolve(LocalContext.current)` or similar helpers.

### 7. Coding Style
- **Naming**: Use descriptive names. Suffix classes with their role (e.g., `Interactor`, `Repository`, `Presenter`).
- **Types**: Prefer explicit types for public APIs.
- **Immutability**: Use `kotlinx.collections.immutable` for state collections.

### 8. Testing
- **Prefer Fakes over Mocks**: Use hand-written fakes for repositories and interactors.
- **Testing Modules**: Fakes should reside in a `:testing` module corresponding to the feature or layer.
- **Navigator**: Use `TestNavigator` for asserting navigation events in presenter tests.
- **Turbine**: Use Turbine for testing flows.

### 9. Integration Testing
- **Location**: Integration tests reside in the `:core:integration` module.
- **TestGraph**: Use `TestGraph` to resolve dependencies. It provides a real dependency graph with specific components replaced by fakes (e.g., Network, DataStore).
- **Execution**:
    - Use `runTestWithGraph { graph -> ... }` to run tests. This helper handles setting up the `TestGraph` and the `TestDispatcher`.
    - Ensure `Lifecycle.destroy()` is called and `advanceUntilIdle()` is used when testing presenters to prevent coroutine leaks.
- **Platform Specifics**: Metro's graph factories are materialized per target. JVM and iOS variants of helpers must live in their respective source sets.
- **Android**: Use `androidHostTest` for running integration tests on the JVM while having access to Android resources if needed.

## Development Workflow
1. **Feature Addition**:
   - Define `Route` in `nav`.
   - Implement `Interactor` in `domain` (if needed).
   - Implement `Presenter` in `presenter` using `@AssistedInject` and `@NavScreen`.
   - Implement `Screen` in `ui` using `@ScreenUi`.
2. **Data Changes**:
   - Update SQLDelight `.sq` files.
   - Update `Store` configuration.
   - Update `Mapper` to convert database/network entities to domain/UI models.

## Verification
- Run `./gradlew lint` for Android linting.
- Run `./gradlew test` for unit tests.
- Ensure all DI graphs are valid by running a build.
