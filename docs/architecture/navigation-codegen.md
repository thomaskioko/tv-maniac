# Navigation Codegen

## Table of Contents

- [Annotations](#annotations)
- [Route as scope](#route-as-scope)
- [Patterns by destination kind](#patterns-by-destination-kind)
- [Tab pager page](#tab-pager-page)
- [Parent-owned child presenter](#parent-owned-child-presenter)
- [App root pair](#app-root-pair)
- [Manual components](#manual-components)
- [Configuration](#configuration)

The navigation codegen processor reads project annotations at compile time and emits the [Metro](glossary.md#metro) graph extensions, navigation destination bindings, and UI multibinding contributions that connect each presenter to the navigation system. The processor is a [KSP](glossary.md#ksp) plugin shipped in [`io.github.thomaskioko.gradle.plugins:codegen-processor`](https://github.com/thomaskioko/app-gradle-plugins).

## Annotations

Each annotation in the tables below is defined by the project. The processor reads them at compile time and emits the listed artifacts.

### Shared KMP layer

| Annotation | Generated artifacts |
|---|---|
| `@NavDestination(route, parentScope, kind = SCREEN)` | `<Presenter>ScreenGraph`. A binding that contributes a `NavDestination.Screen` and a `NavRouteBinding<*>` into their respective sets at `parentScope`. |
| `@NavDestination(route, parentScope, kind = OVERLAY)` | Same shape as `SCREEN`, but the binding contributes a `NavDestination.Overlay`. The route class must additionally implement [`OverlayRoute`](glossary.md#overlayroute) so `Navigator.navigateTo` dispatches to the slot. |
| `@NavDestination(route, parentScope, kind = TAB_ROOT)` | `<Presenter>TabGraph`. A binding that contributes a `NavDestination.TabRoot`, a `NavRootBinding<*>`, and the route singleton into `Set<NavRoot>` at `parentScope`. |
| `@ChildPresenter(scope, parentScope)` | `<Presenter>ChildGraph` graph extension exposing the presenter, plus a nested `@ContributesTo(parentScope) @GraphExtension.Factory` whose `create<BaseName>Graph` function takes a `ComponentContext` and returns the graph. Used for parent-owned child presenters such as tab pager pages. |
| `@AppRoot(parentScope)` | `<RootInterface>BindingContainer`. A `@BindingContainer @ContributesTo(parentScope)` object that wires `DefaultRootPresenter.Factory` to the bound interface at `parentScope`. |

### Android UI layer

| Annotation | Generated artifacts |
|---|---|
| `@ScreenUi` | `ScreenContent` binding under `ui/di/` for the annotated composable. |
| `@SheetUi` | `SheetContent` binding under `ui/di/` for the annotated composable. |
| `@TabUi` | `ScreenContent` binding for a tab pager page. Predicate matches `TabChild<*>` rather than `ScreenDestination<*>`. |
| `@AppRootUi(presenter, parentScope)` | `AppRootProvider` interface declaring one property per non-modifier parameter on the host composable, plus a `@Composable AppRootProvider.AppRootContent(modifier)` extension. The activity-scope `@DependencyGraph` extends `AppRootProvider`. |

## Route as scope

Generated graph extensions use the route class itself as the scope marker (`@GraphExtension(DebugRoute::class)`). The route doubles as the scope token, so no separate marker class is needed. The factory contributes to `parentScope`, typically [`ActivityScope::class`](glossary.md#activityscope).

## Patterns by destination kind

### Simple stack screen

Plain `@Inject`. The processor emits a graph that exposes the presenter directly.

```kotlin
@Inject
@NavDestination(
    route = DebugRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
public class DebugPresenter(componentContext: ComponentContext) : ComponentContext by componentContext
```

### Parameterized stack screen

[`@AssistedInject`](glossary.md#assistedinject) with exactly one `@Assisted` parameter. The parameter name matches the single property on the route class.

```kotlin
@Serializable
public data class ShowDetailsRoute(public val param: ShowDetailsParam) : NavRoute

@AssistedInject
@NavDestination(
    route = ShowDetailsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
public class ShowDetailsPresenter(
    @Assisted private val param: ShowDetailsParam,
    componentContext: ComponentContext,
    // ...
)
```

### Overlay

Identical shape to `SCREEN`. The route class additionally implements `OverlayRoute`. The processor emits `NavDestination.Overlay`. `DefaultRootPresenter` filters that subclass for the overlay slot.

```kotlin
@Serializable
public data class EpisodeSheetRoute(public val param: EpisodeSheetParam) : NavRoute, OverlayRoute

@AssistedInject
@NavDestination(
    route = EpisodeSheetRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.OVERLAY,
)
public class EpisodeSheetPresenter(
    @Assisted private val param: EpisodeSheetParam,
    componentContext: ComponentContext,
    // ...
)
```

### Tab root

Plain `@Inject`. The route is a `NavRoot` `data object`. The processor emits `NavDestination.TabRoot`, a `NavRootBinding<*>` for save and restore, and the route singleton into `Set<NavRoot>` so navigators can enumerate tabs without consulting destination factories.

```kotlin
@Serializable
public data object DiscoverRoot : NavRoot

@Inject
@NavDestination(
    route = DiscoverRoot::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.TAB_ROOT,
)
public class DiscoverShowsPresenter(componentContext: ComponentContext) : ComponentContext by componentContext
```

### Tab pager page

Bottom-bar tabs (Discover, Library, Progress, Profile) are wrapped as `TabChild` rather than `ScreenDestination`. Annotate the tab composable with `@TabUi` so the generated predicate casts to `TabChild<*>`.

```kotlin
@Composable
@TabUi(presenter = DiscoverShowsPresenter::class, parentScope = ActivityScope::class)
public fun DiscoverScreen(
    presenter: DiscoverShowsPresenter,
    modifier: Modifier = Modifier,
) { /* ... */ }
```

### Parent-owned child presenter

Use `@ChildPresenter` on a presenter constructed by another presenter rather than navigated to through a route. The parent host takes one factory per child and instantiates each child with `Decompose.childContext(key)`.

```kotlin
@Inject
@ChildPresenter(
    scope = ProgressChildScope::class,
    parentScope = ProgressRoot::class,
)
public class UpNextPresenter(componentContext: ComponentContext) : ComponentContext by componentContext

@Inject
@NavDestination(route = ProgressRoot::class, parentScope = ActivityScope::class, kind = DestinationKind.TAB_ROOT)
public class ProgressPresenter(
    componentContext: ComponentContext,
    upNextGraphFactory: UpNextChildGraph.Factory,
    calendarGraphFactory: CalendarChildGraph.Factory,
) : ComponentContext by componentContext {
    public val upNextPresenter: UpNextPresenter =
        upNextGraphFactory.createUpNextGraph(childContext(key = "UpNext")).upNextPresenter
    public val calendarPresenter: CalendarPresenter =
        calendarGraphFactory.createCalendarGraph(childContext(key = "Calendar")).calendarPresenter
}
```

The factory function name embeds the base name (`createUpNextGraph`, `createCalendarGraph`) so two child graphs contributing to the same `parentScope` do not collide.

### Embeddable / reusable component

`parentScope` decides which hosts can embed the child. The example above pins `UpNextPresenter` to the progress tab because `parentScope` is `ProgressRoot::class`. A component meant to live in its own module and be reused across screens (for example a featured-shows hero embedded in both Discover and Search) instead sets `parentScope` to `ActivityScope::class`.

Metro graph extensions resolve bindings from any ancestor scope, and every screen descends from `ActivityScope` (`AppScope -> ActivityScope -> {tab roots} -> {child scopes}`). A factory contributed to `ActivityScope` is therefore visible to every tab root, stack screen, and child below it, so any host can inject it and embed the component with the same two lines shown above.

```kotlin
@Inject
@ChildPresenter(
    scope = FeaturedShowsComponentScope::class,
    parentScope = ActivityScope::class,
)
public class FeaturedShowsPresenter(componentContext: ComponentContext) : ComponentContext by componentContext
```

The generator is scope-agnostic, so this needs no annotation or codegen change: it is purely the `parentScope` chosen. The one constraint is that an embeddable component may inject only bindings reachable at `ActivityScope` or `AppScope`; depending on a tab-root-scoped binding fails as an ordinary Metro missing-binding error at the embedding site. Nesting one embeddable component inside another works for free, because the outer component's graph also descends from `ActivityScope`.

On the UI side the component's composable is a plain `@Composable` that takes the child presenter and is called directly by the host (the host already holds the child presenter, so there is no nav-stack entry to dispatch). It carries no UI codegen annotation: `@ScreenUi`/`@SheetUi`/`@TabUi` exist only for routed destinations the navigation host renders from `Set<ScreenContent>`. An embedded component is rendered by its host, not the navigation host, so there is no rule requiring an annotation on it.

### App root pair

The activity-scope root presenter and its host composable use `@AppRoot` and `@AppRootUi`. The activity graph extends the generated `AppRootProvider` interface, and `MainActivity` invokes `graph.AppRootContent()` instead of forwarding each dependency to `RootScreen` by hand. There is one such pair per project.

## Manual components

The processor handles every presenter and UI binding. Two things still need to be written manually.

- **Routes and roots**: every `NavRoute` and `NavRoot` class is written manually. Routes define the public navigation API of each feature module.
- **Marker-only destinations**: needed when a route has no presenter class. `GenreShowsNavDestinationBinding` is the only manual `NavDestination.Screen` binding in this codebase.

## Configuration

Enable codegen in a presenter module by applying `scaffold { useCodegen() }` in `build.gradle.kts`. UI modules that contribute `@ScreenUi`, `@SheetUi`, or `@TabUi` bindings additionally need `api(projects.navigation.ui)`. Modules that consume `@ChildPresenter`-generated graphs need `implementation(projects.features.<host>.nav)` so the parent and child scopes resolve. A host embedding a reusable component (the `ActivityScope` variant above) depends instead on that component's own module, which carries both its scope marker and its generated `<Component>ChildGraph.Factory`.
