# Navigation Codegen

## Table of Contents

- [Why codegen exists](#why-codegen-exists)
- [Annotations](#annotations)
- [Route as scope](#route-as-scope)
- [Patterns by destination shape](#patterns-by-destination-shape)
- [What stays hand-written and why](#what-stays-hand-written-and-why)
- [How this extends the navigation module structure](#how-this-extends-the-navigation-module-structure)
- [Where the source lives](#where-the-source-lives)
- [Next Steps](#next-steps)

> **What this covers**: the KSP annotation processor that eliminates per-destination boilerplate by generating
> `@GraphExtension` interfaces and destination bindings from a single annotation on the presenter class.
> **Prerequisites**: read [Navigation](navigation.md) and [Dependency Injection](dependency-injection.md) first. The
> codegen builds on top of the patterns defined there: `NavRoute`, `NavDestination`, `NavRouteBinding`, `SheetConfig`,
> `SheetChildFactory`, `SheetConfigBinding`, Metro graph extensions, and the route-as-scope decision are all explained
> in those docs.

## Why codegen exists

Before the processor existed, adding a root-stack screen required four hand-written artifacts per destination. Using
the debug screen as the running example:

The first is `DebugRoute` in `features/debug/nav/api/`: the route is the feature's public API. Its shape is an
intentional decision the feature author controls. It stays hand-written.

The remaining three were mechanical: a `DebugScreenScope` scope-marker class in `nav/api/scope/`, a `DebugScreenGraph`
interface carrying `@GraphExtension(DebugScreenScope::class)` and a `@GraphExtension.Factory` in `presenter/di/`, and a
`DebugNavDestinationBinding` interface in `presenter/di/` that contributed a `NavDestination` and a
`NavRouteBinding<*>` into the activity-scope multibinding sets.

The scope, the graph, and the binding are all entirely determined by the route type and the presenter class. They
contain no feature-specific logic. The codegen collapses those three artifacts into a single annotation on the
presenter class. The route is the only thing left to write.

Sheet destinations follow the same shape with a parallel set of generated types. The episode sheet previously required
an `EpisodeSheetConfig` (hand-written, public API), an `EpisodeSheetScreenScope`, an `EpisodeSheetScreenGraph`, and an
`EpisodeSheetDestinationBinding` that contributed a `SheetChildFactory` and a `SheetConfigBinding<*>`. The annotation
reduces that to the config class plus the annotation on the presenter.

## Annotations

The three annotations cover every current destination shape in the app. They live in the `codegen-annotations` library
from the `app-gradle-plugins` composite build. Applying the shared scaffold convention to a presenter module is enough
to make them available; no manual wiring is needed beyond that.

### `@NavScreen`

```kotlin
public annotation class NavScreen(
    val route: KClass<out NavRoute>,
    val parentScope: KClass<*> = ActivityScope::class,
)
```

Place this on a presenter class to generate a `@GraphExtension` interface scoped to the route class (see
[Route as scope](#route-as-scope) below) and a `@ContributesTo(parentScope)` binding interface with two
`@Provides @IntoSet` methods: one contributing a `NavDestination` and one contributing a `NavRouteBinding<*>`.

`parentScope` defaults to `ActivityScope`. Override it only when the presenter lives inside a scope narrower than the
activity, which is rare for root-stack destinations.

### `@TabScreen`

```kotlin
public annotation class TabScreen(
    val config: KClass<out HomeConfig>,
    val parentScope: KClass<*> = HomeScreenScope::class,
)
```

Place this on a tab presenter class to generate a `@GraphExtension` interface scoped to the `HomeConfig` subtype and a
`@ContributesTo(parentScope)` binding interface contributing a single `TabDestination` to `Set<TabDestination>`. Tab
destinations do not produce a `NavRouteBinding` because `HomeConfig` serialization is owned by the Home module.

### `@NavSheet`

```kotlin
public annotation class NavSheet(
    val route: KClass<out SheetConfig>,
    val parentScope: KClass<*> = ActivityScope::class,
)
```

Place this on a sheet presenter class to generate a `@GraphExtension` interface scoped to the `SheetConfig` subtype
and a `@ContributesTo(parentScope)` binding interface with two `@Provides @IntoSet` methods: one contributing a
`SheetChildFactory` and one contributing a `SheetConfigBinding<*>`.

The generated `SheetChildFactory` implementation casts the incoming `SheetConfig` to the declared config type before
passing params to the presenter factory. When the presenter uses `@AssistedInject`, the cast extracts the assisted
param from the config property whose name matches the single `@Assisted` constructor parameter.

> [!IMPORTANT]
> The annotations and the processor are only available when the presenter module applies the shared scaffold
> convention that enables codegen. No other manual Gradle wiring is needed in the feature module.

## Route as scope

One of the most consequential architectural decisions in this system is that the generated `@GraphExtension` does not
declare a new scope class. The route class (or `HomeConfig` subtype for tabs, or `SheetConfig` subtype for sheets)
itself serves as the scope marker.

Before the codegen, each destination had a hand-written scope marker (for example, `DebugScreenScope` in
`features/debug/nav/api/scope/`) and the generated graph referenced it as the `@GraphExtension` scope. After the
codegen, the route is the scope, so those scope marker classes are no longer generated or needed.

This works because the route is already hand-written, public, and lives in `nav/api`. Any module that imports
`nav/api` automatically imports the scope for free. There is no KSP per-target-source-set visibility problem: if a
module can see the route (which it already must, to push it), it can see the scope. This mirrors the Khonshu
convention.

## Patterns by destination shape

### Simple presenter with `@Inject`

For a presenter with no runtime-supplied parameters, annotate with `@NavScreen`. The processor generates the graph
and the destination binding:

```kotlin
// features/debug/presenter/DebugPresenter.kt
@Inject
@NavScreen(route = DebugRoute::class)
public class DebugPresenter(
    componentContext: ComponentContext,
    private val navigator: DebugNavigator,
) : ComponentContext by componentContext { /* ... */ }
```

The processor generates a graph interface that exposes the presenter as a property and a factory that receives a
`ComponentContext`:

```kotlin
// generated: DebugScreenGraph.kt
@GraphExtension(DebugRoute::class)
public interface DebugScreenGraph {
    public val debugPresenter: DebugPresenter

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createDebugGraph(@Provides ctx: ComponentContext): DebugScreenGraph
    }
}
```

The generated binding contributes both a `NavDestination` and a `NavRouteBinding<*>` into the activity-scope
multibinding sets. The `NavDestination.createChild` calls the graph factory and wraps the returned presenter in a
`ScreenDestination`.

### Parameterized presenter with `@AssistedInject`

When the presenter needs a runtime parameter extracted from the route, use `@AssistedInject` with exactly one
`@Assisted` constructor parameter. The processor detects the `@AssistedFactory` nested interface and generates a graph
that exposes the factory. The generated `NavDestination.createChild` casts the incoming route and extracts the param
by the matching property name:

```kotlin
// features/show-details/presenter/ShowDetailsPresenter.kt
@AssistedInject
@NavScreen(route = ShowDetailsRoute::class)
public class ShowDetailsPresenter(
    @Assisted private val param: ShowDetailsParam,
    componentContext: ComponentContext,
) {
    @AssistedFactory
    public fun interface Factory {
        public fun create(param: ShowDetailsParam): ShowDetailsPresenter
    }
}
```

The generated destination fragment looks like this:

```kotlin
override fun createChild(route: NavRoute, ctx: ComponentContext): RootChild {
    val showRoute = route as ShowDetailsRoute
    val graph = graphFactory.createShowDetailsGraph(ctx)
    return ScreenDestination(graph.showDetailsFactory.create(showRoute.param))
}
```

The `@Assisted` parameter name and the route property name must match. The `ShowDetailsPresenter` declares
`@Assisted val param`, so `ShowDetailsRoute` must declare a property named `param` of a compatible type. If they do
not match, the processor emits a compile-time error naming the offending class. Restructure assisted params into a
single data class (as `ShowDetailsParam` does here) when the route carries more than one value.

### Tab presenter with `@TabScreen`

```kotlin
// features/discover/presenter/DiscoverShowsPresenter.kt
@Inject
@TabScreen(config = HomeConfig.Discover::class)
public class DiscoverShowsPresenter(/* ... */) { /* ... */ }
```

The generated `TabDestination.createChild` calls the graph factory and returns a `TabChild` wrapping the presenter:

```kotlin
override fun createChild(config: HomeConfig, ctx: ComponentContext): TabChild<*> {
    val graph = graphFactory.createDiscoverTabGraph(ctx)
    return TabChild(graph.discoverPresenter)
}
```

No `NavRouteBinding` is generated for tab presenters. The Home module owns `HomeConfig` serialization and is
responsible for registering its own subtypes.

### Sheet presenter with `@NavSheet`

```kotlin
// features/episode-sheet/presenter/EpisodeSheetPresenter.kt
@AssistedInject
@NavSheet(route = EpisodeSheetConfig::class)
public class EpisodeSheetPresenter(
    @Assisted private val episodeId: Long,
    @Assisted private val source: String,
    componentContext: ComponentContext,
) {
    @AssistedFactory
    public fun interface Factory {
        public fun create(episodeId: Long, source: String): EpisodeSheetPresenter
    }
}
```

The generated binding contributes both a `SheetChildFactory` and a `SheetConfigBinding<*>`. The
`SheetChildFactory.matches` body checks for the specific config type, and `createChild` casts and extracts the params
before calling the presenter factory:

```kotlin
override fun matches(config: SheetConfig): Boolean = config is EpisodeSheetConfig

override fun createChild(config: SheetConfig, componentContext: ComponentContext): SheetChild {
    val sheetConfig = config as EpisodeSheetConfig
    return SheetDestination(
        presenter = graphFactory.createEpisodeDetailGraph(componentContext)
            .episodeDetailFactory.create(sheetConfig.episodeId, sheetConfig.source),
    )
}
```

## What stays hand-written and why

Route classes like `DebugRoute` and `ShowDetailsRoute`, and sheet configs like `EpisodeSheetConfig`, all in
`features/{name}/nav/api/`, are always written by the feature author. The route is the feature's public API: its
shape, its serialized form, and its properties are intentional decisions. The codegen generates code that depends on
the route, not the other way around.

Feature navigator interfaces in `nav/api/` are also hand-written. A stateful navigator exposes typed methods that
represent the feature's public navigation contract. The set of methods, their names, and their parameters are domain
decisions, not mechanical ones.

`GenreShowsNavDestinationBinding` in `features/genre-shows/presenter/di/` remains hand-written because Genre Shows has
no presenter class to annotate. Its destination is a marker that the root presenter handles directly through the
multibinding set. One bespoke binding does not justify a new annotation shape, and the codegen annotations require a
class target.

> [!TIP]
> If a feature has no presenter class at all and only needs to contribute a destination marker, keep the binding
> hand-written. The annotations are designed for the case where a presenter class drives the destination, and the
> generated code wraps that presenter.

## How this extends the navigation module structure

The multibinding sets documented in [Navigation](navigation.md) (`Set<NavDestination>`, `Set<NavRouteBinding<*>>`,
`Set<SheetChildFactory>`, `Set<SheetConfigBinding<*>>`) all live in `ActivityScope`. Hand-written bindings contribute
to these sets using `@ContributesTo(ActivityScope::class)` interfaces with `@Provides @IntoSet` companion methods.

The codegen is the author-facing way to contribute to those same sets. Annotating a presenter with `@NavScreen`
generates the `@ContributesTo` interface and the `@Provides @IntoSet` methods automatically. The root presenter and
the navigation runtime are not affected by whether a contribution was generated or hand-written: they only see the
multibinding sets. The codegen introduces no new runtime machinery. It removes the boilerplate for feeding the
existing machinery.

As a feature author, you just add the annotation to your presenter, and the codegen will generate all the necessary
DI bindings for navigation. You don't need to write the repetitive DI code yourself. The navigation system works the
same way whether the bindings are generated or hand-written. Codegen just saves you time and reduces errors.

The DI graph validates that all multibinding contributions form a consistent set at compile time. A missing
`NavRouteBinding`, a missing `NavDestination`, or a missing `SheetChildFactory` surfaces as a Metro error before any
runtime crash.

## Where the source lives

The annotations and the KSP processor live in the
[`app-gradle-plugins`](https://github.com/thomaskioko/app-gradle-plugins) composite build under `codegen/`. That
composite contains three subprojects: `codegen/annotations/` (the KMP library), `codegen/processor/` (the JVM KSP
processor), and `codegen/processor-test/` (compile-testing fixtures for each annotation shape and error-path
diagnostics). See the `codegen/` README inside `app-gradle-plugins` for processor internals, golden fixture layout,
and how to extend the annotation set.

## Next Steps

- [Navigation](navigation.md) - Core navigation primitives: `NavRoute`, `Navigator`, `SheetConfig`, destination
  factories, the multibinding sets that the generated bindings contribute into, and the `navigation/testing` helpers
  (`TestNavigator`, `NavigatorTurbine`) for declarative navigation assertions in presenter tests.
- [Modularization](modularization.md) - Module archetypes, the "Adding a New Feature" checklist, and where generated
  files land relative to hand-written code.
- [Dependency Injection](dependency-injection.md) - Metro scopes, `@GraphExtension`, and how the generated graph
  extensions are wired into the parent scope.
