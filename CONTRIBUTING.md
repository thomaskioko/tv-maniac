# Contributing to TvManiac

TvManiac is a personal learning playground for Kotlin Multiplatform development. Contributions that fix bugs,
improve documentation, or demonstrate KMP patterns are welcome.

## Prerequisites

| Tool | Version |
|---|---|
| JDK | 21 (Zulu recommended: https://www.azul.com/downloads/?package=jdk#zulu) |
| Android Studio | Latest stable or canary |
| Xcode | 16.4 (iOS contributions) |
| KMM Plugin | Latest compatible with the Kotlin version in `gradle/libs.versions.toml` |

CocoaPods is not used. The iOS app consumes the shared KMP framework as an XCFramework built by Gradle.

## Cloning and Initial Setup

```bash
git clone https://github.com/thomaskioko/tv-maniac.git
cd tv-maniac
./scripts/install-git-hooks.sh
```

The git hooks run Spotless formatting checks before each commit. A commit is blocked if formatting fails.
Fix formatting with `./gradlew spotlessApply` and then re-commit.

### API Keys

The app requires TMDB and Trakt credentials at build time. See [docs/setup.md](docs/setup.md) for how to
obtain them. Create `local.properties` in the project root:

```properties
TMDB_API_KEY=your_tmdb_api_key
TRAKT_CLIENT_ID=your_trakt_client_id
TRAKT_CLIENT_SECRET=your_trakt_client_secret
TRAKT_REDIRECT_URI=tvmaniac://callback
```

This file is gitignored. Do not commit it.

## Building

**Android (debug):**

```bash
./gradlew :app:assembleDebug
```

For a faster local build that skips the iOS XCFramework:

```bash
./gradlew assembleDebug -Ptvmaniac.debugOnly=true
```

**iOS:**

Open `ios/tv-maniac.xcodeproj` in Xcode 16.4, select a simulator or device, and run.

## Running Tests

```bash
# JVM unit tests (fast, no device needed)
./gradlew jvmTest

# Android connected tests (requires a running emulator or device)
./gradlew connectedAndroidTest

# iOS simulator tests
./gradlew iosSimulatorArm64Test -Papp.enableIos=true
```

## Code Style

**Kotlin:**

- 2-space indentation, 140-character line length.
- Use `ImmutableList` and `toImmutableList()` from `kotlinx.collections.immutable` for state classes.
- No try-catch blocks that silently swallow errors. Propagate exceptions up to the presentation layer.
- Fakes, not mocks. Each `data/*/testing` module provides fake implementations for tests.
- Test names follow the pattern: `should X given Y`. Do not include function names in test names.
- Spotless enforces formatting. Run `./gradlew spotlessApply` before pushing.

**Swift:**

- Follow the Swift API Design Guidelines.
- Format with SwiftFormat. Check with `fastlane ios check_swift_format` before pushing.
- Do not add business logic to SwiftUI views. Views consume shared KMP presenters only.

**General:**

- No comments in code unless the intent cannot be expressed through naming.
- Always specify access modifiers on all Kotlin declarations.
- Business logic belongs in shared KMP code (`domain/*`, `data/*`), never in platform UI layers.

## Branching and Pull Requests

1. Fork the repository and create your branch from `main`.
2. Keep branches focused on a single concern. Large refactors should be discussed in an issue first.
3. Ensure CI passes: Spotless, unit tests, and lint checks must all be green.
4. Include a clear description in your PR: what changed, why, and how to test it.
5. Reference any related issue with `Closes #123` or `Relates to #123` in the PR body.

PRs that add new architectural patterns should also update the relevant file under `docs/architecture/`.

## Architecture Overview

Before contributing, read the architecture docs so your change fits the existing patterns:

- [Modularization](docs/architecture/modularization.md)
- [Presentation Layer](docs/architecture/presentation-layer.md)
- [Data Layer](docs/architecture/data-layer.md)
- [Navigation](docs/architecture/navigation.md)
- [Dependency Injection](docs/architecture/dependency-injection.md)

## Filing Bugs and Questions

Open an issue on GitHub. Use the bug report template for reproducible defects and the feature request
template for new ideas. For open-ended questions, open a GitHub issue with the `question` label.
