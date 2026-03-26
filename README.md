<p align="center">
<img src="art/TvManiacBanner.png" width="100%" />
</p>

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

## Install

Download the latest APK from [GitHub Releases](https://github.com/thomaskioko/tv-maniac/releases).

Or stay up to date with the latest alpha builds via [Firebase App Distribution](https://appdistribution.firebase.dev/i/564c934cc970634b).

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

### Setup & Build

```bash
./scripts/install-git-hooks.sh
```

**Android:**
```bash
./gradlew :app:assembleDebug
```

**iOS:**
Open `ios/tv-maniac.xcodeproj` in Xcode and run.

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

## Gradle Convention Plugins

Build configurations are managed by [app-gradle-plugins](https://github.com/thomaskioko/app-gradle-plugins), a set of custom Gradle convention plugins published to Maven Central. They handle Android/KMP module setup, versioning, release automation, and R8 optimization. For a deep dive into how they work, see [Publishing Gradle Convention Plugins](https://thomaskioko.me/posts/publishing_gradle_plugins/).

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
