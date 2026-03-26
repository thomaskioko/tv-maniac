# TvManiac

![Check](https://github.com/thomaskioko/tv-maniac/actions/workflows/ci.yml/badge.svg)
![kmp](https://img.shields.io/badge/multiplatform-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![compose](https://img.shields.io/badge/jetpack_compose-2bab6b.svg?style=for-the-badge&logo=android&logoColor=white)
![swiftui](https://img.shields.io/badge/swiftui-%23000000.svg?style=for-the-badge&logo=swift&logoColor=white)

[![Download APK](https://img.shields.io/github/v/release/thomaskioko/tv-maniac?label=Download%20APK&logo=android&style=for-the-badge)](https://github.com/thomaskioko/tv-maniac/releases/latest)

**TvManiac** is a personalized entertainment tracking and recommendation Multiplatform app (Android & iOS) for tracking TV Shows. By utilizing [Trakt](https://trakt.tv) and [TMDB](https://developer.themoviedb.org/docs), you can discover shows, manage your watchlist, track watch progress, and get personalized recommendations.

| Android | iOS |
|---|---|
| <video src="https://github.com/user-attachments/assets/90ec7924-7d50-40a4-bb0b-89d79aa9bbcd" width=350/> | <video src="https://github.com/user-attachments/assets/69f101b7-e100-4775-9893-6687e455560c" width=350/> |

> **Under Heavy Development**
>
> This is my playground for learning Kotlin Multiplatform. With that said, I'm sure it's filled with bugs crawling everywhere, and I'm probably doing a couple of things wrong. So a lot is changing, but that shouldn't stop you from checking it out.

---

## Getting Started

### Requirements

- [Zulu Java 21](https://www.azul.com/downloads/?package=jdk#zulu)
- Latest [Android Studio](https://developer.android.com/studio/preview)
- [KMM Plugin](https://kotlinlang.org/docs/multiplatform-mobile-setup.html)

### API Keys

The app requires TMDB and Trakt API credentials. See [docs/setup.md](docs/setup.md) for detailed instructions.

Create `local.properties` in the project root:

```properties
TMDB_API_KEY=your_tmdb_api_key
TRAKT_CLIENT_ID=your_trakt_client_id
TRAKT_CLIENT_SECRET=your_trakt_client_secret
TRAKT_REDIRECT_URI=tvmaniac://callback
```

### Build & Run

**Android:**
```bash
./gradlew :app:assembleDebug
```

**iOS:**
Open `ios/tv-maniac.xcodeproj` in Xcode and run.

### Git Hooks

```bash
./scripts/install-git-hooks.sh
```

Installs a pre-commit hook that runs Spotless formatting checks.

---

## Architecture

The project follows Clean Architecture with a modular design organized by feature and layer. Business logic and state management live in shared KMP code, while Android (Compose) and iOS (SwiftUI) contain only UI rendering.

For detailed documentation:

- [Modularization](docs/architecture/MODULARIZATION.md)
- [Presentation Layer](docs/architecture/PRESENTATION_LAYER.md)
- [Data Layer](docs/architecture/DATA_LAYER.md)
- [Navigation](docs/architecture/NAVIGATION.md)
- [Dependency Injection](docs/architecture/DI.md)

---

## Tech Stack

**Shared (KMP)**
- [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Concurrency
- [Ktor](https://ktor.io/) - Networking
- [SQLDelight](https://github.com/cashapp/sqldelight) - Local database
- [Decompose](https://arkivanov.github.io/Decompose/) - Navigation and lifecycle
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) - JSON serialization
- [kotlin-inject-anvil](https://github.com/amzn/kotlin-inject-anvil) - Dependency injection
- [Multiplatform Paging](https://github.com/cashapp/multiplatform-paging) - Pagination

**Android**
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI toolkit
- [Coil](https://coil-kt.github.io/coil/) - Image loading
- [AppAuth](https://openid.github.io/AppAuth-Android/) - OAuth authentication

**iOS**
- [SwiftUI](https://developer.apple.com/xcode/swiftui/) - UI framework
- [Nuke](https://github.com/kean/Nuke) - Image loading
- [OAuthSwift](https://github.com/OAuthSwift/OAuthSwift) - OAuth authentication

---

## Releases

Production releases will be published to the Google Play Store and App Store once the release pipeline is fully configured.

Alpha builds are distributed daily via [Firebase App Distribution](https://firebase.google.com/docs/app-distribution) to internal testers. Contributors can request access by opening an issue.

APKs for each production release are attached to [GitHub Releases](https://github.com/thomaskioko/tv-maniac/releases).

For the full release process, see [release/RELEASE.md](release/RELEASE.md).

---

## Gradle Convention Plugins

The project uses custom Gradle convention plugins to maintain consistent build configurations across modules. For an in-depth guide, see [Publishing Gradle Convention Plugins](https://thomaskioko.me/posts/publishing_gradle_plugins/).

---

## References & Inspiration

- [Design Inspiration](https://dribbble.com/shots/7591814-HBO-Max-Companion-App-Animation)
- [Tivi](https://github.com/chrisbanes/tivi)
- [Compose Samples](https://github.com/android/compose-samples)

## License

```
Copyright 2021 Thomas Kioko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
