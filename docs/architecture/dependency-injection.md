# Dependency Injection

## Table of Contents

- [Scope Hierarchy](#scope-hierarchy)
- [Naming Conventions](#naming-conventions)
- [Binding Containers](#binding-containers)
- [Qualifiers](#qualifiers)
- [Assisted Injection](#assisted-injection)
- [App Initializers](#app-initializers)
- [Graph Creation](#graph-creation)
- [API / Implementation Boundary](#api--implementation-boundary)
- [Testing](#testing)
- [Adding a New Injectable](#adding-a-new-injectable)

> **What this covers**: scopes, dependency graphs, graph extensions, binding containers, qualifiers, assisted injection, and how tests swap in fakes.
> **Prerequisites**: read [Modularization](modularization.md) for the api / implementation boundary. Metro is summarised in the root README [Key Concepts](../../README.md#key-concepts).

The project uses [Metro](https://zacsweers.github.io/metro/latest/) for compile-time dependency injection. Metro is a Kotlin compiler plugin that treats aggregation as a first-class citizen, so there is no KSP processor and no runtime reflection. Every binding is resolved at graph-processing time.

The primary entry points in Metro are **dependency graphs**: interfaces annotated with `@DependencyGraph` that expose types from the object graph via accessor properties or functions. Those accessors act as the roots from which the rest of the graph is resolved.

## Scope Hierarchy

```mermaid
graph TD
    subgraph AS["AppScope"]
        direction LR
        R["Repositories / Stores / Clients / Database"]
    end

    subgraph ACS["ActivityScope"]
        direction LR
        RN["Navigator / Stateful controllers"]
    end

    subgraph SS["ScreenScope"]
        direction LR
        P["Presenters (no-param)"]
        PF["Presenter Factories (with-param)"]
    end

    subgraph TS["TabScope"]
        direction LR
        TP["Tab Presenters"]
    end

    AS ==> ACS
    ACS ==> SS
    SS ==> TS

    style AS fill:#FF9800,color:#fff,stroke:#E65100,stroke-width:2px
    style ACS fill:#4CAF50,color:#fff,stroke:#1B5E20,stroke-width:2px
    style SS fill:#2196F3,color:#fff,stroke:#0D47A1,stroke-width:2px
    style TS fill:#9C27B0,color:#fff,stroke:#4A148C,stroke-width:2px
```

> [!NOTE]
> Scope lifecycle maps directly to Decompose component lifecycle. When a Decompose `ComponentContext` is destroyed (a screen is popped, an activity is finished), the Metro scope that was created with that context is also destroyed. Bindings scoped to that level are garbage-collected. This alignment is what makes per-screen presenters safe to hold expensive resources: they are cleaned up at the same point the user leaves the screen.

Scopes form a hierarchy. Each child scope inherits every binding from its parent. See [Scope Hierarchy](scopes.md) for the full scope tree, including per-screen scope, tab scope, and nested child scope, along with how each scope is created from its parent via `@GraphExtension.Factory`.

### AppScope

Application-wide singletons. Created once and shared across the entire app lifetime.

**What lives here**
- Repositories and their Store instances
- Database and DAO instances
- Network API clients (TMDB, Trakt)
- Request manager (cache validation)
- Datastore (preferences)
- Logger, Localizer, and other utilities

### ActivityScope

Activity-lifetime instances. Created when the root activity is created. Destroyed when the activity is destroyed.

**What lives here**
- The `Navigator` and `SheetNavigator` interfaces and their implementations
- The root presenter
- Stateful controllers (sheet host, tab host)

Presenters and presenter factories do not live in `ActivityScope`. They live in per-screen scopes that descend from it. See [Scope Hierarchy](scopes.md) for the full tree.

### TestScope

Lives alongside `AppScope` in tests. The `TestJvmGraph` / `TestIosGraph` root graphs target `AppScope` and swap in fakes via `FakeAppBindings`. See [Testing](#testing).

## Naming Conventions

Metro distinguishes three DI concepts. This project maps them to explicit suffixes so there is exactly one place to look for each shape.

Classes annotated with `@DependencyGraph` use the `*Graph` suffix (for example: `ApplicationGraph`, `IosApplicationGraph`, `TestJvmGraph`). These are the entry points an app, activity, or test creates to access its wired types.

Classes annotated with `@GraphExtension` also use the `*Graph` suffix (for example: `ActivityGraph`, `IosViewPresenterGraph`). These are child graphs scoped to a narrower lifetime that inherit every binding from their parent.

Classes annotated with both `@BindingContainer` and `@ContributesTo` use the `*BindingContainer` suffix (for example: `BaseBindingContainer`, `TmdbBindingContainer`, `NavigationBindingContainer`). These are `public object`s that group `@Provides` methods and contribute them to a scope, used for bindings that `@ContributesBinding` cannot express.

Decompose (the navigation/presenter library) has its own `Component` and `ComponentContext` types. Reusing `*Component` for DI classes would create constant ambiguity at every reference site. The Metro-aligned naming keeps the two domains disjoint: DI classes are always `*Graph` or `*BindingContainer`, and anything named `Component` (`ComponentContext`, `DefaultComponentContext`, the Swift `ComponentHolder<T>` helper) belongs to Decompose.

## Binding Containers

A binding container groups related `@Provides` methods into a single object and contributes them to a scope. In this project, every binding container is a `public object` annotated with `@BindingContainer` and `@ContributesTo(SomeScope::class)`:

```kotlin
@BindingContainer
@ContributesTo(AppScope::class)
public object BaseBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main,
    )
}
```

Rules:
- **`@BindingContainer` must be on a `public object`**, never an `interface`. Metro rejects `@BindingContainer` on interfaces with direct `@Provides` methods.
- Prefer `@ContributesBinding` on the implementation class itself when you're binding an interface to a concrete class. Metro describes it as contributing "injected classes to a target scope as a given bound type", which is exactly what the repository and data-source classes in this project do.
- Reach for a binding container only when `@ContributesBinding` can't express the binding: platform types, third-party types, qualified `@Provides`, factory methods, or bindings that need explicit construction.

> [!WARNING]
> By default Metro respects Kotlin visibility: `internal` is module-scoped, so `@ContributesBinding` on an `internal` class is invisible to graphs in other modules. Metro then reports a missing-binding error at the entry-point graph (for example `ApplicationGraph`) rather than at the feature module. Metro's `generateContributionProviders` feature flag removes this restriction by generating top-level `@Provides` wrappers, but this project does not enable it. Declare `@ContributesBinding` implementations as `public class`.

## Qualifiers

Metro identifies every binding by a **type key**: the concrete type plus any qualifier annotation attached to it. Two bindings of the same type with different qualifiers are distinct, so qualifiers are the project's way of disambiguating otherwise-identical types (multiple `CoroutineScope`s, multiple `HttpClientEngine`s, Android `Context`).

The project defines its qualifiers in `core/base` (`Qualifiers.kt`):

- `@ApplicationContext`: Android `Context` injection sites that want the application context.
- `@TmdbApi`, `@TraktApi`: `HttpClientEngine` and related Ktor types, splitting TMDB vs Trakt networking.
- `@MainCoroutineScope`: `CoroutineScope` bound to the main dispatcher.
- `@IoCoroutineScope`: `CoroutineScope` bound to the IO dispatcher.
- `@ComputationCoroutineScope`: `CoroutineScope` bound to the computation dispatcher.
- `@Initializers`: multibinding set for synchronous app initializers.
- `@AsyncInitializers`: multibinding set for asynchronous app initializers.

The qualifier goes on both the `@Provides` site and every injection site:

```kotlin
@Inject
public class DefaultTraktAuthRepository(
    @IoCoroutineScope private val ioScope: CoroutineScope,
    @MainCoroutineScope private val mainScope: CoroutineScope,
) : TraktAuthRepository
```

## Assisted Injection

Metro describes assisted injection as the mechanism "for types that require dynamic dependencies at instantiation". Presenters that have screen-specific parameters (show ID, season params) use assisted injection. Metro matches assisted parameters by parameter name, so no explicit `@Assisted("id")` values are needed.

### Presenters without screen parameters

Most presenters use plain `@Inject`. Their `ComponentContext` is provided by a `@GraphExtension` scope (see [Scope Hierarchy](scopes.md) for the full scope tree). No Factory interface needed.

```kotlin
@Inject
public class HomePresenter(
    componentContext: ComponentContext,              // provided by ScreenScope
    homeTabNavigator: HomeTabNavigator,
    private val tabDestinations: Set<TabDestination>,
    private val observeUserProfileInteractor: ObserveUserProfileInteractor,
) : ComponentContext by componentContext
```

Parent code resolves these directly from the graph: `screenGraph.homePresenter`.

To see how `HomePresenter` is exposed from its per-screen graph extension, including the `ComponentContext` provision and the accessor property the root presenter calls, read [`HomeScreenGraph.kt`](../../features/home/presenter/src/commonMain/kotlin/com/thomaskioko/tvmaniac/presenter/home/di/HomeScreenGraph.kt).

### Presenters with screen parameters

Presenters that need runtime parameters beyond `ComponentContext` use `@AssistedInject` with a Factory. Only the screen-specific params are `@Assisted`. `ComponentContext` comes from the scope.

```kotlin
@AssistedInject
public class ShowDetailsPresenter(
    componentContext: ComponentContext,              // provided by ScreenScope
    @Assisted private val param: ShowDetailsParam,  // screen-specific
    private val navigator: Navigator,
    private val showDetailsInteractor: ShowDetailsInteractor,
) : ComponentContext by componentContext {

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: ShowDetailsParam): ShowDetailsPresenter
    }
}
```

Parent code gets the factory from the graph: `screenGraph.showDetailsFactory.create(param)`.

## App Initializers

In Metro, multibindings are "collections of bindings of a common type" that are implicitly declared by the existence of `@IntoSet` / `@IntoMap` providers. The project uses two qualified `Set<() -> Unit>` multibindings to coordinate startup work:

- `@Initializers`: runs inline at startup. Use for lightweight, synchronous setup that must complete before the first UI frame (logger, locale, Coil `ImageLoader`).
- `@AsyncInitializers`: runs inside a coroutine launched on `@IoCoroutineScope`. Use for anything touching disk or network (token refresh, sync schedulers, cache warming).

Both sets are declared once in `core/base` with `@Multibinds` so Metro knows they exist even if a given module contributes nothing:

```kotlin
@ContributesTo(AppScope::class)
public interface InitializerMultibindings {
    @Initializers @Multibinds public fun initializers(): Set<() -> Unit>
    @AsyncInitializers @Multibinds public fun asyncInitializers(): Set<() -> Unit>
}
```

To add a new initializer, write a plain `@Inject` class with an `init()` method and contribute a lambda into the correct set from a sibling binding container. `AppInitializers.initialize()` iterates each set at startup. No registration code to touch, no application-class edits.

## Graph Creation

The project uses a long-lived application graph, a short-lived graph extension per activity or per iOS view, and throwaway test graphs per test class.

### Android

`ApplicationGraph` is the `@DependencyGraph(AppScope::class)`. `TvManicApplication` creates it once with the `Application` instance and holds it for the lifetime of the process:

```kotlin
@DependencyGraph(AppScope::class)
public interface ApplicationGraph {
    public val initializers: AppInitializers
    public val workerFactory: TvManiacWorkerFactory

    @DependencyGraph.Factory
    public fun interface Factory {
        public fun create(@Provides application: Application): ApplicationGraph
    }
}
```

`ActivityGraph` is a `@GraphExtension(ActivityScope::class)` accessed through the parent graph via `asContribution<Factory>()`:

```kotlin
public companion object {
    public fun create(activity: ComponentActivity): ActivityGraph =
        (activity.application as TvManicApplication)
            .getApplicationGraph()
            .asContribution<Factory>()
            .createGraph(activity)
}
```

### iOS

`IosApplicationGraph` is the iOS root graph, created from `AppDelegate`. It exposes a `IosViewPresenterGraph.Factory` that Swift calls to produce per-view graphs.

```kotlin
@DependencyGraph(AppScope::class)
public interface IosApplicationGraph {
    public val initializers: AppInitializers
    public val viewPresenterGraphFactory: IosViewPresenterGraph.Factory

    public companion object {
        public fun create(): IosApplicationGraph = createGraph<IosApplicationGraph>()
    }
}
```

Swift holds the root graph in `AppDelegate` and wraps the per-view graph in `ComponentHolder<IosViewPresenterGraph>`.

## API / Implementation Boundary

The DI system enforces the module dependency rules described in [Modularization](modularization.md):

1. **Interfaces in `api/` modules**: repository interfaces, data source interfaces, and models live in `data/*/api/`.
2. **Implementations in `implementation/` modules**: concrete classes live in `data/*/implementation/` and are bound to their interfaces via `@ContributesBinding`.
3. **Consumers depend on `api/` only**: presenters, interactors, and other modules import the interface, never the implementation.

When a presenter asks for a `LibraryRepository`, Metro supplies `DefaultLibraryRepository` without the consuming module ever importing the implementation module.

## Testing

Tests build their own dependency graph that reuses the production aggregation and swaps in fakes.

- `TestJvmGraph` / `TestIosGraph`: `@DependencyGraph(AppScope::class)` interfaces that expose the presenters and repositories a test needs via accessor properties.
- `FakeAppBindings`: a `@BindingContainer public object` that uses `@ContributesTo(AppScope::class, replaces = [...])` to override specific production bindings (auth, datastore, crash reporter, background task scheduler, etc.) with fakes from the `testing` modules.
- `FakeIosPlatformBindings`: same pattern for iOS-specific platform bindings.
- `TestJvmGraphTest`: a smoke test that instantiates the graph and verifies every presenter factory resolves, catching DI regressions in a single JVM test.

Every `data/*/testing` module provides a fake implementation. Tests never use mocks.

## Adding a New Injectable

The general pattern for any new injectable class:

1. **Define the interface** in the `api/` module.
2. **Implement it** in the `implementation/` module.
3. **Annotate** the implementation with `@Inject` and `@ContributesBinding(AppScope::class)`. Add `@SingleIn(AppScope::class)` if it should be a singleton.
4. **Inject** the interface wherever it's needed. Metro resolves it at graph-processing time.

For presenters, use `@AssistedInject` with an `@AssistedFactory fun interface Factory` for runtime parameters. For platform types or third-party classes that Metro can't `@Inject` directly, add a `*BindingContainer` `public object` with `@Provides` methods and contribute it to the scope with `@ContributesTo`.

## Next Steps

- [Scope Hierarchy](scopes.md) - The full Metro scope tree, how each scope is created from its parent, and what lives in each scope.
- [Navigation](navigation.md) - How per-screen graph extensions integrate with the Decompose navigation stack.
