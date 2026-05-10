# Glossary

This document defines every cross-cutting term used in `docs/architecture/` and `docs/contributing/`. Every page links here on first reference.

## Libraries and Tools

### Decompose

A Kotlin Multiplatform navigation library. Holds navigation state in shared code so Android Compose and SwiftUI render the same back stack. Reference: [`https://arkivanov.github.io/Decompose/`](https://arkivanov.github.io/Decompose/).

### Metro

A compile-time dependency injection compiler plugin for Kotlin. Resolves the object graph at build time without reflection. Reference: [`https://zacsweers.github.io/metro/latest/`](https://zacsweers.github.io/metro/latest/).

### SQLDelight

A Kotlin Multiplatform library that generates type-safe Kotlin APIs from SQL files. Used as the persistence layer. Reference: [`https://cashapp.github.io/sqldelight/`](https://cashapp.github.io/sqldelight/).

### KSP

Kotlin Symbol Processing. Generates Kotlin source from annotations at compile time. Powers the project's navigation code generation.

### Moko Resources

A Kotlin Multiplatform library that exposes Android XML strings to iOS through a generated `MR.strings` accessor. Consumed by `Localizer`.

### Robolectric

An Android testing library that runs Android framework calls on the JVM. Used by integration tests so they execute without an emulator.

### Ktor

A Kotlin Multiplatform HTTP client. The project configures one Ktor client for each upstream service.

## Navigation

### BaseRoute

The sealed parent of every routable target. Every concrete navigation entry implements either `NavRoute` or `NavRoot`.

### NavRoute

A back stack entry. Pushed onto the active tab through `Navigator.navigateTo`. Subtypes that also implement `OverlayRoute` go to the overlay slot instead.

### NavRoot

A tab anchor. Each `NavRoot` owns one back stack. Switched through `Navigator.switchBackStack` or `Navigator.showRoot`.

### OverlayRoute

A route that activates the overlay slot rather than pushing onto the active tab.

### Navigator

The single contributed interface that mutates navigation state. Hides every Decompose `StackNavigation` and `SlotNavigation` source behind one surface.

### NavDestination

A typed link from a route to its presenter. The codegen processor generates one entry for each route. One manual binding lives in `genre-shows`.

### Multi-stack navigation

The custom Decompose state holder that keeps one back stack for each tab. Implemented as `MultiStackNavState` in `navigation/implementation`.

### Slot

A Decompose component slot that holds at most one active child. Used for overlays such as bottom sheets.

### ChildStack and ChildSlot

Decompose types used for read-only navigation state. `ChildStack` holds a back stack with the topmost child active. `ChildSlot` holds at most one active child for slot navigation.

## Data

### Store

A coordinator that combines a [`Fetcher`](#fetcher), a [`SourceOfTruth`](#sourceoftruth), and a [`Validator`](#validator) into a single read API.

### Fetcher

The network side of a `Store`. Calls Trakt or TMDB and returns domain models.

### SourceOfTruth

The persistence side of a `Store`. Reads from and writes to SQLDelight.

### Validator

The freshness check on a `Store`. Decides whether cached data is still acceptable.

### RequestManagerRepository

The component that records fetch timestamps. Stores compare elapsed time against thresholds in their `Validator` to decide if a network call is required.

### Interactor

A use case wrapper around one or more repositories. The project ships two flavors: `SubjectInteractor` for streaming reads and `Interactor` for one-shot operations.

## Presentation

### Presenter

A shared Kotlin Multiplatform class that exposes a `StateFlow` of screen state and accepts dispatched actions. Both Android and iOS bind to the same presenter.

### RootPresenter

The top-level presenter constructed at `ActivityScope`. Owns the multi-stack host and the overlay slot.

### Localizer

The interface presenters use to resolve Moko Resources string keys to platform-localized strings.

## Dependency Injection

### AppScope

Application-wide scope. Holds repositories, stores, the database, and Ktor clients.

### ActivityScope

Activity-lifetime scope. Holds the `Navigator` and the `RootPresenter`.

### ScreenScope

Per-screen scope. Holds the screen presenter and its factory.

### TabScope

Per-tab scope. Holds the tab presenter for each `NavRoot`.

### GraphExtension

A Metro annotation that marks a graph as a child of a parent graph. Used to nest `ScreenScope` under `ActivityScope` and `TabScope` under `ScreenScope`.

### AssistedInject

A Metro injection mode that lets a class take both injected dependencies and runtime parameters. Used for presenters that receive a navigation parameter such as a show identifier.

### BindingContainer

A `public object` annotated with `@BindingContainer` that groups `@Provides` methods. Used for third-party types and qualified providers.

### Qualifier

An annotation that disambiguates identical types in the graph. Examples: `@TmdbApi`, `@TraktApi`, `@IoCoroutineScope`.

## Testing

### Robot

A test helper that wraps a `ComposeContentTestRule` and exposes high-level steps such as `tapShow(name)`. Used by integration and journey tests.

### Journey test

A test that runs an end-to-end user lifecycle across multiple surfaces, for example sign in followed by trending followed by watchlist. Built on top of the integration testing harness.
