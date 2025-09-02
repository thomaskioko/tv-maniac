TvManiac
-------------------------
![Check](https://github.com/thomaskioko/tv-maniac/actions/workflows/ci.yml/badge.svg)
![kmp](https://img.shields.io/badge/multiplatform-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![compose](https://img.shields.io/badge/jetpack_compose-2bab6b.svg?style=for-the-badge&logo=android&logoColor=white)
![swiftui](https://img.shields.io/badge/swiftui-%23000000.svg?style=for-the-badge&logo=swift&logoColor=white)

**TvManiac** is a personalized entertainment tracking and recommendation Multiplatform app. By utilizing
[TMDB](https://developer.themoviedb.org/docs), we can view shows, create a watchlist get statistics, and much more. This project aims to
demonstrate KMP development capabilities. This is currently running on:

- Android: Compose
- iOS: SwiftUI

## 🚧 Under Heavy Development 🚧

This is my playground for learning Kotlin Multiplatform. With that said, I'm sure it's filled with bugs crawling everywhere, and I'm
probably doing a couple of things wrong. So a lot is changing, but that shouldn't stop you from checking it out.

| Android                                                                                                  | iOS                                                                                                      |
|----------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| <video src="https://github.com/user-attachments/assets/90ec7924-7d50-40a4-bb0b-89d79aa9bbcd" width=350/> | <video src="https://github.com/user-attachments/assets/69f101b7-e100-4775-9893-6687e455560c" width=350/> |

> [!IMPORTANT]
> To fetch data, you will need to [create a TMDB API app](https://www.themoviedb.org/settings/api) and generate an API key if you don't have
> one. Each platform has its own resource key file:
>
> - **Android**: Add your API keys to `core/util/src/androidMain/resources/dev.yaml`
> - **iOS**: Add your API keys to `ios/ios/Resources/dev.yaml`
>
> In both files, replace the placeholder values with your actual API keys.

## 🖥 Project Setup & Environment

#### Requirements

- [Zulu Java 21](https://www.azul.com/downloads/?package=jdk#zulu)
- You require the latest [Android Studio](https://developer.android.com/studio/preview) release to be able to build the app.
- Install KMM Plugin. Checkout [this setup guide](https://kotlinlang.org/docs/multiplatform-mobile-setup.html).

### Opening iOS Project

- Navigate to the ios directory & open `.xcodeproj`

### Git Hooks

The project uses Git hooks to enforce code quality checks before commits. To install the hooks, run:

```bash
./scripts/install-git-hooks.sh
```

This will install the following hooks:

- **pre-commit**: Runs code formatting checks using Spotless before allowing a commit. If the checks fail, the commit
  will be aborted.

## Architecture Overview

- [ ] TODO: Add detail architecture

![TvManiac Architecture](https://github.com/thomaskioko/tv-maniac/assets/841885/84e314fc-71a5-40e5-b034-213e6b546f9a)

## Gradle Convention Plugins

The project uses custom Gradle convention plugins to maintain consistent build configurations across modules. These plugins help standardize dependencies,
compiler settings, and build logic throughout the codebase. For an in-depth guide on how these convention plugins are created, structured, and published to Maven Central,
check out the article: [Publishing Gradle Convention Plugins](https://thomaskioko.me/posts/publishing_gradle_plugins/).

## Libraries Used

### Android

* [Accompanist](https://github.com/google/accompanist)
  * [Insets](https://google.github.io/accompanist/insets/)
  * [Pager Layouts](https://google.github.io/accompanist/pager/)
* [Android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player) - Youtube Player
* [AppAuth](https://openid.github.io/AppAuth-Android/) - AppAuth for Android is a client SDK for communicating with OAuth 2.0 and OpenID
  Connect providers.
* [Compose Lints](https://slackhq.github.io/compose-lints/) - Custom lint checks for Jetpack Compose.
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
  * [Coil](https://coil-kt.github.io/coil/compose/) - Image loading
* [KenBurnsView](https://github.com/flavioarfaria/KenBurnsView) - Immersive image.
* [Leakcanary](https://github.com/square/leakcanary) - Memory leak detection.

### Kmp - Common

* [Coroutines](https://github.com/Kotlin/kotlinx.coroutines#multiplatform) - Concurrency & Threading
* [DataStore Preferences](https://android-developers.googleblog.com/2022/10/announcing-experimental-preview-of-jetpack-multiplatform-libraries.html) -
  Data storage
* [DateTime](https://github.com/Kotlin/kotlinx-datetime) - Date & Time
* [Decompose](https://arkivanov.github.io/Decompose/) - Kotlin Multiplatform library for breaking down your code into lifecycle-aware
  business logic components (aka BLoC).
* [Kermit](https://kermit.touchlab.co/) - Logging
* [kotlin-inject-anvil](https://github.com/amzn/kotlin-inject-anvil?tab=readme-ov-file) - Dependency Injection library.
* [Kotlinx Serialization](https://ktor.io/docs/kotlin-serialization.html) - De/Serializing JSON
* [Ktor](https://ktor.io/) - Networking
* [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html) - Testing
* [Multiplatform Paging](https://github.com/cashapp/multiplatform-paging) A library that adds additional Kotlin/Multiplatform targets to
  AndroidX Paging, and provides UI components to use Paging on iOS.
* [SQLDelight](https://github.com/cashapp/sqldelight/) - Local storage
  - [Coroutines Extensions](https://cashapp.github.io/sqldelight/js_sqlite/coroutines/) Consume queries as Flow
* [Paging](https://developer.android.com/jetpack/androidx/releases/paging) -

### iOS

* [SDWebImage](https://github.com/SDWebImage/SDWebImage) - Async image downloader.
* [OAuthSwift](https://github.com/OAuthSwift/OAuthSwift) Swift-based OAuth library for iOS and macOS.
* [Youtube PlayerKit](https://github.com/SvenTiigi/YouTubePlayerKit) - Swift Youtube Player

## Roadmap

Android

- [x] Implement Watchlist
- [x] Add `More` screen. Shows GridView
- [x] Recommended Shows
- [x] Implement pagination.
- [x] Add Settings panel.
  - Dynamic theme change.
- [x] Add Seasons UI
- [x] Implement trakt auth & sign in
- [x] Migrate to Material3
- [x] Implement Paging
- [x] Implement Search
- [ ] Add Episode detail screen

iOS

- [x] Add HomeScreen: Tabs & Empty UI
- [x] Implement Discover UI
- [x] Show Detail Screen
- [x] Add Settings panel.
- [x] Implement trakt auth & sign in
- [x] Update show detail UI
- [x] Add Seasons Detail UI
- [x] Implement Paging
- [x] Implement Search
- [ ] Add Episode detail screen

Shared

- [x] Use SQLDelight extensions to consume queries as Flow
- [x] Refactor interactor implementation.
- [x] Use koin for injection
- [x] Modularize `shared` module
- [x] Try out [Flow-Redux](https://github.com/freeletics/FlowRedux)
- [ ] Fix paging
- [x] Add test cases.
- [ ] Improve error handling.

### References & Inspiration

- [Design Inspiration](https://dribbble.com/shots/7591814-HBO-Max-Companion-App-Animation)
- [Code Snippets](https://github.com/android/compose-samples)
- [Touchlab KaMPKit project](https://github.com/touchlab/KaMPKit)
- [Tivi](https://github.com/chrisbanes/tivi)

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
