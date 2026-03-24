# Changelog

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-03-25

Initial beta release of TvManiac.

### Features

- **Discover** — Browse trending, popular, top-rated, and upcoming TV shows from TMDB
- **Show Details** — View show info, seasons, cast, trailers, watch providers, and similar shows
- **Season Details** — Episode list with air dates, ratings, and watch status tracking
- **Search** — Search shows by title with genre-based content categories
- **Library** — Track followed shows with status filters (Returning, Ended, etc.)
- **Watchlist** — Manage shows you plan to watch
- **Up Next** — Track next episodes to watch across all followed shows
- **Trakt Integration** — Full OAuth2 authentication with automatic token refresh
- **Profile** — View Trakt profile and sync activity
- **Settings** — Theme selection (light/dark/system) and app preferences
- **Trailers** — Watch show trailers via embedded YouTube player

### Platform Support

- **Android** — Jetpack Compose UI with Material 3 design system
- **iOS** — SwiftUI with native navigation and platform conventions
- **Shared** — Business logic, data layer, and presenters in Kotlin Multiplatform

### Architecture

- Clean Architecture with modular design organized by feature and layer
- Store pattern for data fetching with offline-first caching
- Decompose for shared navigation across platforms
- SQLDelight for local database with 20 migrations
- Ktor for networking with exponential backoff and rate limiting
- kotlin-inject for dependency injection with compile-time safety

### Infrastructure

- CI pipeline with Android build, lint, JVM tests, iOS build, SwiftLint, and snapshot tests
- Release automation with version bumping, changelog generation, and store deployment
- Single `version.txt` source of truth for both platforms
- Firebase Crashlytics for crash reporting
- Background tasks for token refresh and library sync (WorkManager + BGTaskScheduler)
- Localization support for English, German, and French
