# Architecture

TvManiac is a Kotlin Multiplatform (KMP) entertainment tracking app that shares business logic and data layers across Android (Jetpack Compose) and iOS (SwiftUI). The architecture follows Clean Architecture principles with a modular design organized by feature and layer.

![TvManiac Architecture](https://github.com/thomaskioko/tv-maniac/assets/841885/84e314fc-71a5-40e5-b034-213e6b546f9a)

## Table of Contents

| Document | Description |
|---|---|
| [Modularization](MODULARIZATION.md) | Module archetypes, dependency rules, and how features are organized |
| [Presentation Layer](PRESENTATION_LAYER.md) | Shared presenters, state management, and platform UI binding |
| [Data Layer](DATA_LAYER.md) | Store pattern, caching strategy, and the hybrid API approach |
| [Navigation](NAVIGATION.md) | Decompose-based shared navigation across platforms |
| [Dependency Injection](DI.md) | Scope hierarchy and module wiring principles |
