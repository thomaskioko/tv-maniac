# Navigation

## Table of Contents

- [Core Components](#core-components)
- [Rendering Destinations](#rendering-destinations)
- [Navigator Pattern](#navigator-pattern)
- [Feature Communication](#feature-to-feature-communication)
- [Testing](#testing-navigation)

Uses [Decompose](https://arkivanov.github.io/Decompose/) for shared navigation. State is managed in KMP; platform UI renders the current screen.

> [!IMPORTANT]
> Depend on `navigation/api` only. `navigation/implementation` is restricted to the entry-point graph.

## Core Components

### Root Presenter
Owns the root `ChildStack` and modal `ChildSlot`. Iterates multibinding sets to resolve children for routes. Lives in `features/root/presenter`.

### Routes vs Sheets
- **Stack Screen** (`NavRoute`): Full destination. Pushes to back stack.
- **Sheet** (`SheetConfig`): Modal overlay. Only one active at a time.

### Navigator
Primary API for presenters. Pushes routes by type.
- `pushNew(route)`: Push new screen.
- `pop()`: Remove top screen.
- `bringToFront(route)`: Bring existing screen to front or push.
- `pushToFront(route)`: Push and remove previous occurrences.

### SheetNavigator
Sheet-side counterpart. Manages `SlotNavigation<SheetConfig>`.
- `activate(config)`: Activates sheet.
- `dismiss()`: Dismisses active sheet.

### Children
- `RootChild`: Marker for stack children.
- `SheetChild`: Marker for sheet children.
- `ScreenDestination<T>`, `SheetDestination<T>`: Generic presenter wrappers.

### Multibindings
- `Set<NavDestination>` / `Set<SheetChildFactory>`: Matchers and child builders.
- `Set<NavRouteBinding<*>>` / `Set<SheetConfigBinding<*>>`: Route classes and serializers.

## Rendering Destinations

### Android: `Set<ScreenContent>` / `Set<SheetContent>`
Registries in `navigation/ui` coordinate rendering.
- **`@ScreenUi` / `@SheetUi`**: Annotations on composable functions to generate di bindings.
- **Root Screen**: Receives content sets and delegates rendering.

### iOS: `ScreenRegistry`
Swift registry populated at startup in `ScreenRegistryBootstrap.swift`. `RootNavigationView` consumes the registry.

## Navigator Pattern

Presenters inject `Navigator` from `navigation/api`.
- **Stateful Navigation**: Use per-feature navigator interfaces (e.g., tab switching).
- **Default**: Inject `Navigator` directly and push routes from other feature `nav` modules.

## Feature Communication

1. **Push Route**: Feature A pulls feature B's `nav` module and pushes its route.
2. **Stateful Navigator**: Inject shared interface (e.g., `SheetNavigator`) bound at `ActivityScope`.

> [!WARNING]
> Presenter-to-presenter and UI-to-UI dependencies across features are prohibited.

## Testing

Uses `navigation/testing`.
- **`TestNavigator`**: Records `NavEvent` calls.
- **`NavigatorTurbine`**: Wraps Turbine for ordered event consumption.
- **`FakeSheetNavigator`**: Records `activate` and `dismiss` calls.

```kotlin
testNavigator.test {
    presenter.dispatch(Action)
    awaitPushNew(ExpectedRoute())
}
```
