# Navigation Codegen

## Table of Contents

- [Annotations](#annotations)
- [Route as Scope](#route-as-scope)
- [Patterns by Destination Shape](#patterns-by-destination-shape)

KSP annotation processor that eliminates navigation boilerplate by generating `@GraphExtension` interfaces, destination bindings, and UI multibinding contributions.

## Annotations

### Shared KMP Layer
- **`@NavScreen`**: Generates graph extension and `NavDestination` + `NavRouteBinding`. Use for root-stack screens.
- **`@TabScreen`**: Generates graph extension and `TabDestination`. Use for home tab presenters.
- **`@NavSheet`**: Generates graph extension and `SheetChildFactory` + `SheetConfigBinding`. Use for modal sheets.

### Android UI Layer
- **`@ScreenUi`**: Generates `ScreenContent` binding in `ui/di/`.
- **`@SheetUi`**: Generates `SheetContent` binding in `ui/di/`.

## Route as Scope

Generated `@GraphExtension` interfaces use the route class (or `HomeConfig`/`SheetConfig` subtype) as the scope marker. This eliminates the need for manual scope marker classes.

## Patterns by Destination Shape

### Simple Presenter
Annotate with `@NavScreen`. Processor generates graph and `NavDestination`.

```kotlin
@Inject
@NavScreen(route = DebugRoute::class)
public class DebugPresenter(...)
```

### Parameterized Presenter
Use `@AssistedInject` with one `@Assisted` parameter. Parameter name must match the route property name.

```kotlin
@AssistedInject
@NavScreen(route = ShowDetailsRoute::class)
public class ShowDetailsPresenter(@Assisted private val param: ShowDetailsParam, ...)
```

### Sheet Presenter
Annotate with `@NavSheet`. Generates `SheetChildFactory` that performs the cast and extracts params.

```kotlin
@AssistedInject
@NavSheet(route = EpisodeSheetConfig::class)
public class EpisodeSheetPresenter(...)
```

## Manual Components

- **Routes and Configs**: Always manual; they define the public API.
- **Navigator Interfaces**: Manual if stateful.
- **Bespoke Bindings**: Manual if no presenter class exists (e.g., marker-only destinations).

## Configuration

Enable by applying `scaffold { useCodegen() }` in `build.gradle.kts`. `ui` modules require `api(projects.navigation.ui)`.
