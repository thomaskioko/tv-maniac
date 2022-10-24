TvManiac
-------------------------
![Check](https://github.com/c0de-wizard/tv-maniac/actions/workflows/build.yml/badge.svg)  ![android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat) ![ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)

**TvManiac** is a Multiplatform app (Android & iOS) for viewing TV Shows information from
[TMDB](https://www.themoviedb.org/). The aim of this project is do demonstrate KMM development capabilities.

You can Install and test latest android app from below 👇

[![TvManiac Debug](https://img.shields.io/badge/Debug--Apk-download-green?style=for-the-badge&logo=android)](https://github.com/c0de-wizard/tv-maniac/releases/latest/download/app-debug.apk)


## 🚧 Under Heavy Development 🚧
This is my playground for learning Kotlin Multiplatform. With that said, I'm sure it's filled bugs are crawling everywhere, and I'm probably doing a couple of things wrong. So a lot is changing, but that shouldn't stop you from checking it out.

### Android Screenshots

<table>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/AndroidHomeLight.png?raw=true" alt="Home Screen Light" width="500"/>
    </p>
  </td>
    <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/AndroidDetailLight.png?raw=true" alt="Home Screen Light" width="500"/>
    </p>
  </td>
    <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/AnroidHomeDark.png?raw=true" alt="Home Screen Dark" width="500"/>
    </p>
  </td>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/AnroidDetailDark.png?raw=true" alt="Show Details Dark" width="500"/>
    </p>
  </td>
</tr>
</table>

### 🔆 iOS Screenshots

<table>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/iOSHomeLight.png?raw=true" alt="Home Screen Light" width="500"/>
    </p>
  </td>
    <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/iOSDetailLight.png?raw=true" alt="Home Screen Light" width="500"/>
    </p>
  </td>
    <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/iOSHomeDark.png?raw=true" alt="Home Screen Dark" width="500"/>
    </p>
  </td>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/iOSDetailDark.png?raw=true" alt="Show Details Dark" width="500"/>
    </p>
  </td>
</tr>
</table>


## 🖥 Project Setup & Environment

### Api Keys

You need to add API keys from [Trakt.tv](https://trakt.docs.apiary.io) & [TMDb](https://developers.themoviedb.org). To do so:

- Create `local.properties` in `root` dir
- Add the following
    ```
    TMDB_API_URL=https://api.themoviedb.org/3/
    TMDB_API_KEY=ENTER_URI
    TRAKT_CLIENT_ID=ENTER_KEY
    TRAKT_CLIENT_SECRET=ENTER_KEY
    TRAKT_REDIRECT_URI=ENTER_KEY
    ```
- Run `./gradlew generateBuildKonfig`


### Android
- Java 11
- You require the latest [Android Studio Dolphin](https://developer.android.com/studio/preview) release to be able to build the app.
- Install Kmm Plugin. Checkout [this setup guide](https://kotlinlang.org/docs/kmm-setup.html).


### Opening iOS Project
- Navigate to ios directory & open `.xcworkspace` & not `.xcodeproj` 

### Genereating Swift Package Locally
In case you make changes to the `shared` module and want to test out the changes, you can generate the swift package locally by:

1. Execute `./gradle createSwiftPackage`. This will generate a swift package outside the root directory.
2. Add the generated package in XCode.


## Project Structure & Architecture
I wrote an article, [Going Modular — The Kotlin Multiplatform Way](https://medium.com/better-programming/going-modular-the-kotlin-multiplatform-way-132c3dee6c95) detailing the modularization process and thinking behind the structure of the shared module.


## Libraries Used
### Android
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
    * [Coil](https://coil-kt.github.io/coil/compose/) - Image loading
    * [Navigation](https://developer.android.com/jetpack/compose/navigation) - Navigation
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) UI related data holder, lifecycle
  aware.
* [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager?gclsrc=ds&gclsrc=ds) Handle persistent work
* [Accompanist](https://github.com/google/accompanist)
    * [Insets](https://google.github.io/accompanist/insets/)
    * [Pager Layouts](https://google.github.io/accompanist/pager/)
* [Dagger Hilt](https://dagger.dev/hilt) - dependency injection.
* [KenBurnsView](https://github.com/flavioarfaria/KenBurnsView) - Immersive image.
* [Leakcanary](https://github.com/square/leakcanary) - Memory leak detection.
* [Android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player) - Youtube Player
* [AppAuth](https://openid.github.io/AppAuth-Android/) - AppAuth for Android is a client SDK for communicating with OAuth 2.0 and OpenID Connect providers.

### Kmp - Common
* [Ktor](https://ktor.io/) - Networking
* [Kotlinx Serialization](https://ktor.io/docs/kotlin-serialization.html) - De/Serializing JSON
* [Coroutines](https://github.com/Kotlin/kotlinx.coroutines#multiplatform) - Concurrency & Threading
* [DateTime](https://github.com/Kotlin/kotlinx-datetime) - Date & Time
* [SQLDelight](https://github.com/cashapp/sqldelight/) - Local storage
    - [Coroutines Extensions](https://cashapp.github.io/sqldelight/js_sqlite/coroutines/) Consume queries as Flow
* [Napier](https://github.com/AAkira/Napier) - Logging
* [Mockk](https://github.com/mockk/mockk) - mocking library for Kotlin.
* [koin](https://github.com/mockk/mockk) - Injection library.

### iOS
* [Kingfisher](https://github.com/onevcat/Kingfisher) - Image library.
* [TvManiac](https://github.com/c0de-wizard/tvmaniac-swift-packages) - TvManiac SwiftPackage.

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
- [ ] UI State improvement.
- [ ] Add Episode detail screen
- [ ] Add Watchlist
- [ ] Implement Search

iOS
- [x] Add HomeScreen: Tabs & Empty UI
- [x] Implement Discover UI
- [x] Show Detail Screen
- [ ] Implement trakt auth & sign in
- [ ] Add Seasons UI
- [ ] Implement Search UI
- [ ] Implement Watchlist UI
- [ ] Implement Load more

Shared
- [x] Use SQLDelight extensions to consume queries as Flow
- [x] Refactor interactor implementation.
- [x] Use koin for injection
- [x] Modularize `shared` module
- [ ] Try out [Flow-Redux](https://github.com/freeletics/FlowRedux) 
- [ ] Improve error handling, add retry.
- [ ] Add test cases.
- [ ] Fix paging
- [ ] Better MVI implementation
- [ ] Observe Internet connection
    - [x] Android
    - [ ] iOS


### References
- [Design Inspiration](https://dribbble.com/shots/7591814-HBO-Max-Companion-App-Animation)
- [Code Snippets](https://github.com/android/compose-samples)
- [Touchlab KaMPKit project](https://github.com/touchlab/KaMPKit)

## License

```
Copyright 2022 Thomas Kioko

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
