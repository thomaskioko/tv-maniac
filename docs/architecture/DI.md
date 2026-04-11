# Dependency Injection

The project uses compile-time dependency injection. This document covers the scope hierarchy, naming conventions, and wiring principles that apply regardless of the specific DI framework or annotations in use.

> The project uses [Metro](https://github.com/ZacSweers/metro) for compile-time dependency injection — a Kotlin compiler plugin with aggregation support.

## Naming Conventions

Metro distinguishes three DI concepts that used to collapse into a single `*Component` name under kotlin-inject / Dagger. This project maps them to explicit suffixes:

| Metro annotation                    | Suffix              | Purpose                                                                                                                                                    | Example                                                                          |
|-------------------------------------|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| `@DependencyGraph`                  | `*Graph`            | A root dependency graph — the object an app/activity/test creates to get its wired dependencies.                                                           | `ApplicationGraph`, `IosApplicationGraph`, `TestJvmGraph`                        |
| `@GraphExtension`                   | `*Graph`            | A child graph scoped to a narrower lifetime that inherits bindings from a parent graph.                                                                    | `ActivityGraph`, `IosViewPresenterGraph`                                         |
| `@ContributesTo` provider interface | `*BindingContainer` | An interface with `@Provides` methods contributed to a scope, used to supply bindings that can't be expressed via `@Inject` / `@ContributesBinding` alone. | `DatabaseBindingContainer`, `TmdbBindingContainer`, `NavigationBindingContainer` |

**Why the split matters here:** Decompose (the navigation/presenter library this project uses) has its own `Component` and `ComponentContext` concept. Using `*Component` for DI classes as well would create constant ambiguity at every reference site. The Metro-aligned naming keeps the two domains disjoint: DI classes are always `*Graph` or `*BindingContainer`, and anything named `Component` (`ComponentContext`, `DefaultComponentContext`, the Swift `ComponentHolder<T>` helper) belongs to Decompose.

When adding new DI code:
- **New root graph?** Use `@DependencyGraph(SomeScope::class)` and name it `*Graph`.
- **New child graph (e.g. a new screen-scoped surface)?** Use `@GraphExtension(SomeScope::class)` and name it `*Graph`.
- **New provider interface for bindings Metro can't figure out on its own?** Use `@ContributesTo(SomeScope::class)` with `@Provides` methods, and name it `*BindingContainer`.
- **Binding a new implementation to an interface?** Prefer `@ContributesBinding` on the implementation class itself — no new file needed.

## Scope Hierarchy

```mermaid
graph TD
    AS["AppScope (Singleton)"] --> ACS["ActivityScope (Screen)"]

    AS -.- R["Repositories"]
    AS -.- DS["Data Sources"]
    AS -.- HC["HTTP Clients"]
    AS -.- DB["Database"]

    ACS -.- P["Presenters"]
    ACS -.- PF["Presenter Factories"]

    style AS fill:#FF9800,color:#fff
    style ACS fill:#4CAF50,color:#fff
```

### AppScope

Application-wide singletons. Created once and shared across the entire app lifetime.

**What lives here:**
- Repositories and their Store instances
- Database and DAO instances
- Network API clients
- Request manager (cache validation)
- Datastore (preferences)
- Logger, Localizer, and other utilities

### ActivityScope

Screen-scoped instances. Created when a screen appears and destroyed when it's removed from the navigation stack.

**What lives here:**
- Presenter factories
- Presenters (created by factories with runtime parameters)

## API / Implementation Boundary

The DI system enforces the module dependency rules described in [Modularization](MODULARIZATION.md):

1. **Interfaces in `api/` modules** — Repository interfaces, data source interfaces, and models are defined in `data/*/api/`
2. **Implementations in `implementation/` modules** — Concrete classes live in `data/*/implementation/` and are bound to their interfaces via DI
3. **Consumers depend on `api/` only** — Presenters, interactors, and other modules import the interface, never the implementation

The DI framework resolves the binding at compile time — when a presenter asks for a `LibraryRepository`, it receives `DefaultLibraryRepository` without ever importing the implementation module.

## Assisted Injection

Presenters require runtime parameters that aren't available at compile time:

- **ComponentContext** — Decompose lifecycle context, provided when the screen is created
- **Navigation callbacks** — Lambdas like `navigateToShowDetails: (Long) -> Unit`, wired by the parent presenter

These are provided through assisted injection: the DI framework creates a factory that accepts the runtime parameters and produces the fully-injected presenter instance.

## Wiring Pattern

The general pattern for any new injectable class:

1. **Define the interface** in the `api/` module
2. **Implement it** in the `implementation/` module
3. **Annotate** the implementation to bind it to the interface within the appropriate scope
4. **Inject** the interface wherever it's needed — the DI framework resolves it automatically

For presenters, add a factory interface in the `api/` module and a factory implementation in the presenter module, using assisted injection for runtime parameters.
