# Navigation

## Table of Contents

- [Type hierarchy](#type-hierarchy)
- [Navigator](#navigator)
- [Destinations](#destinations)
- [Rendering](#rendering)
- [How this differs from idiomatic Decompose](#how-this-differs-from-idiomatic-decompose)
- [Feature communication](#feature-communication)
- [Testing](#testing)

Navigation state lives in shared Kotlin Multiplatform code. Both Android Compose and iOS SwiftUI bind to the same active children. The shared layer is built on [Decompose](glossary.md#decompose).

> [!IMPORTANT]
> Depend on `navigation/api` only. `navigation/implementation` is restricted to the entry-point graph (`ActivityGraph`).

## Type hierarchy

[`BaseRoute`](glossary.md#baseroute) is the sealed parent of every routable target. Two siblings split the surface by role.

- [`NavRoute`](glossary.md#navroute): a back stack entry. Pushed onto the active tab through `Navigator.navigateTo`. Subtypes that also implement [`OverlayRoute`](glossary.md#overlayroute) activate the overlay slot instead.
- [`NavRoot`](glossary.md#navroot): a tab anchor. Each registered `NavRoot` owns one back stack. Switched through `Navigator.switchBackStack` or `Navigator.showRoot`.

Routes are immutable `@Serializable` `data class` or `data object` types declared in each feature's `nav` module, alongside an `@IntoSet NavRouteBinding` (or `NavRootBinding`) so polymorphic save and restore work without a central sealed list.

## Navigator

The [`Navigator`](glossary.md#navigator) is one contributed interface that hides every Decompose `StackNavigation`, `SlotNavigation`, and Generic Navigation source behind a single mutating surface.

| Method | Purpose |
|---|---|
| `navigateTo(route)` | Push a `NavRoute` onto the active tab. Routes implementing `OverlayRoute` go to the overlay slot. |
| `navigateBack()` | Pop the top of the active tab. |
| `navigateBackTo<T>(inclusive)` | Pop until a `T` is on top. |
| `bringToFront(route)` | Class-based dedupe; move to top or push. |
| `pushToFront(route)` | Equality-based dedupe; move to top or push. |
| `popTo(toIndex)` | Pop the active stack to a specific depth. Used by SwiftUI `NavigationStack` path bindings. |
| `switchBackStack(root)` | Switch the active tab to `root`, keep its existing stack. |
| `showRoot(root)` | Switch the active tab to `root`, clear its stack. |
| `replaceAllBackStacks(root)` | Clear every tab to its root entry; activate `root`. |
| `dismissOverlay()` | Programmatically close the active overlay. |
| `buildHostNavigation(ctx, initialRoot, factory)` | Build the multi-stack host once from `HomePresenter`. |
| `buildOverlaySlot(ctx, factory)` | Build the overlay slot once from `DefaultRootPresenter`. |

`activeRoot` is exposed as `Value<NavRoot>` for tab-bar UI.

### Example

A presenter receives the `Navigator` as a constructor dependency and calls it from `dispatch`.

```kotlin
public class TrendingShowsPresenter(
    private val navigator: Navigator,
    // ...
) {
    public fun dispatch(action: TrendingShowsAction) {
        when (action) {
            is ShowClicked -> navigator.navigateTo(
                ShowDetailsRoute(ShowDetailsParam(action.traktId)),
            )
            is OpenLibrary -> navigator.switchBackStack(LibraryRoot)
        }
    }
}
```

The route class lives in `features/<feature>/nav`. The presenter depends on `navigation/api` only and never holds a reference to another presenter or to a Decompose `StackNavigation`.

## Destinations

[`NavDestination<R : BaseRoute>`](glossary.md#navdestination) is sealed with three subclasses. All three are contributed into one `Set<NavDestination<*>>` at [`ActivityScope`](glossary.md#activityscope).

- `Screen<R : NavRoute>`: wraps a screen presenter through `ScreenDestination(presenter)`.
- `Overlay<R : NavRoute>`: wraps an overlay presenter. `DefaultRootPresenter.createOverlay` extracts the presenter and rewraps as `SheetDestination` for the slot child type.
- `TabRoot<R : NavRoot>`: wraps a tab presenter through `TabChild(presenter)`.

Destinations are generated from `@NavDestination(route, parentScope, kind)` (see [`navigation-codegen.md`](navigation-codegen.md)). One `GenreShowsNavDestinationBinding` is written manually for the marker-only `GenreShowsRoute`.

`Set<NavRouteBinding<*>>` and `Set<NavRootBinding<*>>` separately register polymorphic `KSerializer` entries for save and restore. Code generation emits both alongside the destination.

## Rendering

### Android

Two contribution sets in `navigation/ui` drive the Compose UI.

- `Set<ScreenContent>`: matches a `RootChild` (`ScreenDestination` or `TabChild`) and renders it inside the active stack body. Each feature `ui` module contributes one through `@ScreenUi` (routed screens) or `@TabUi` (tab pager pages).
- `Set<SheetContent>`: matches a `SheetChild` (`SheetDestination`) and renders the modal. Each sheet feature contributes one through `@SheetUi`.

`RootScreen` consumes both sets, observes `episodeSheetSlotValue`, and delegates rendering. `HomeScreen` observes one [`ChildStack`](glossary.md#childstack-and-childslot) for each tab from `HomePresenter`, wraps each in a Compose `SaveableStateProvider` keyed by tab name, and renders through `Children`.

### iOS

`ScreenRegistryBootstrap.swift` populates a `ScreenRegistry`. `RootNavigationView` renders the overlay slot through `@StateValue`. `TabBarView` mounts one `DecomposeNavigationStack` for each tab against `Value<ChildStack<*, RootChild>>` and uses `ObjectIdentifier`-stable IDs derived from the `NavigationTab` enum.

## How this differs from idiomatic Decompose

### Multi-stack through a custom `NavState<TabbedRoute>`

Decompose recommends `bringToFront()` on a single `ChildStack` for bottom navigation, or `ChildPages` with one stack for each page. Tv Maniac uses Decompose's Generic Navigation primitive (`componentContext.children()`) with a custom `MultiStackNavState : NavState<TabbedRoute>` that holds `Map<NavRoot, List<BaseRoute>>`. The result keeps a back stack alive for each tab without recreating pages, and keeps overlays on a separate [`SlotNavigation`](glossary.md#slot). The trade-off is a custom state serializer (`MultiStackNavStateSerializer`) and a custom back transformer. Both live in `navigation/implementation` and are unit tested.

### Inactive tab lifecycle equals `CREATED`

Inactive tab entries land in `ChildNavState.Status.CREATED`, not `STARTED` (the Decompose ChildPages default) or `STOPPED` (the Decompose ChildStack default). `CREATED` prevents coroutines tied to `lifecycle.coroutineScope()` from running on inactive tabs, conserving CPU and battery. Tab presenters use `stateIn(..., SharingStarted.WhileSubscribed(...))` for any flow that needs to refresh on tab re-entry. Flows restart on UI subscription regardless of lifecycle status. KDoc lives on `MultiStackNavState.children`.

### Back from a tab root exits the app

The host back transformer returns `null` whenever the active tab's stack contains only its root entry. Pressing back from the root of any tab exits the app, rather than switching to a default home tab. If user research disagrees, swap to a transformer that switches to the first registered tab before returning `null`.

## Feature communication

Push a route from feature B by depending on `features/B/nav` and calling `navigator.navigateTo(BRoute)`. The `Navigator` is the only cross-feature abstraction. Presenters never reach across feature boundaries directly.

> [!WARNING]
> Presenter-to-presenter and UI-to-UI dependencies across features are prohibited.

## Testing

`navigation/testing` ships three test doubles.

- `FakeNavigator`: property-style state inspection (`lastNavigatedRoute`, `lastActivatedOverlay`, `overlayDismissCount`, `lastSwitchedTo`, and others). Use when the test cares about end state.
- `TestNavigator` plus `NavigatorTurbine`: event-flow testing for ordered call assertions.
- `NoOpNavigator`: stand-in for tests that do not observe navigation.

```kotlin
val navigator = FakeNavigator()
presenter.dispatch(SomeAction)
navigator.lastNavigatedRoute shouldBe ExpectedRoute(...)
```
