TvManiac
-------------------------
![Check](https://github.com/c0de-wizard/tv-maniac/actions/workflows/build.yml/badge.svg)  ![android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat) ![ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat) [![TvManiac Debug](https://img.shields.io/badge/Debug--Apk-download-green?style=for-the-badge&logo=android)](https://github.com/c0de-wizard/tv-maniac/releases/latest/download/app-debug.apk)

**TvManiac** is a personalized entertainment tracking and recommendation Multiplatform app. By utilizing
[Trakt](https://trakt.tv/), we can view shows, create a watchlist get statistics, and much more. This project aims to demonstrate KMP development capabilities. This is currently running on:
- Android: Compose
- iOS: SwiftUI


## 🚧 Under Heavy Development 🚧
This is my playground for learning Kotlin Multiplatform. With that said, I'm sure it's filled with bugs crawling everywhere, and I'm probably doing a couple of things wrong. So a lot is changing, but that shouldn't stop you from checking it out.

| Android | iOS | 
| -- | -- | 
| <video src="https://user-images.githubusercontent.com/841885/223576880-c7391d14-63b8-47cd-a7f9-97aee5e47892.mp4" width=350/> | <video src="https://github.com/c0de-wizard/tv-maniac/assets/841885/c26f3857-9b4e-409d-9bfb-6be32cd5111b" width=350/> |


## 🖥 Project Setup & Environment

#### Requirements
- [Zulu Java 17](https://www.azul.com/downloads/?package=jdk#zulu)
- You require the latest [Android Studio](https://developer.android.com/studio/preview) release to be able to build the app.
- Install KMP Plugin. Checkout [this setup guide](https://kotlinlang.org/docs/multiplatform-mobile-setup.html).

### API Keys
To use the Trakt API, you'll need to [create a new API app](https://trakt.tv/oauth/applications/new). & for [TMDb](https://www.themoviedb.org/settings/api), create an account and generate an API key if you don't have one.
Once you have your keys, add them to `config.yaml`. If the file is unavailable, navigate to the root dir and create a symlink.

`ln -s core/util/src/commonMain/resources/config.yaml config.yaml`

```
tmdbApiKey: "PUT_API_KEY_HERE"
traktClientId: "PUT_CLIENT_ID_HERE"
traktClientSecret: "PUT_CLIENT_SECRET_HERE"
traktRedirectUri: "PUT_CALLBACK_URI_HERE"
```

### Opening iOS Project
- Navigate to the ios directory & open `.xcodeproj`

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
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/EpisodeListLight.png?raw=true" alt="Episodes List Light" width="500"/>
    </p>
  </td>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/AnroidHomeDark.png?raw=true" alt="Show Details Dark" width="500"/>
    </p>
  </td>
</tr>
    <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/AnroidDetailDark.png?raw=true" alt="Show Details Dark" width="500"/>
    </p>
  </td>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/EpisodeListDark.png?raw=true" alt="Episodes List Dark" width="500"/>
    </p>
  </td>
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



## Libraries Used
### Android
* [Accompanist](https://github.com/google/accompanist)
  * [Insets](https://google.github.io/accompanist/insets/)
  * [Pager Layouts](https://google.github.io/accompanist/pager/)
* [Android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player) - Youtube Player
* [AppAuth](https://openid.github.io/AppAuth-Android/) - AppAuth for Android is a client SDK for communicating with OAuth 2.0 and OpenID Connect providers.
* [Compose Lints](https://slackhq.github.io/compose-lints/) - Custom lint checks for Jetpack Compose.
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
    * [Coil](https://coil-kt.github.io/coil/compose/) - Image loading
    * [Navigation](https://developer.android.com/jetpack/compose/navigation) - Navigation
* [KenBurnsView](https://github.com/flavioarfaria/KenBurnsView) - Immersive image.
* [Leakcanary](https://github.com/square/leakcanary) - Memory leak detection.
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) UI-related data holder, lifecycle
  aware.
* [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager?gclsrc=ds&gclsrc=ds) Handle persistent work

### Kmp - Common
* [Kotlinx Serialization](https://ktor.io/docs/kotlin-serialization.html) - De/Serializing JSON
* [Coroutines](https://github.com/Kotlin/kotlinx.coroutines#multiplatform) - Concurrency & Threading
* [DataStore Preferences](https://android-developers.googleblog.com/2022/10/announcing-experimental-preview-of-jetpack-multiplatform-libraries.html) - Data storage
* [DateTime](https://github.com/Kotlin/kotlinx-datetime) - Date & Time
* [Flow-Redux](https://github.com/freeletics/FlowRedux)
* [Kermit](https://kermit.touchlab.co/) - Logging
* [kotlin-inject](https://github.com/evant/kotlin-inject) - Injection library.
* [Ktor](https://ktor.io/) - Networking
* [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html) - Testing
* [SQLDelight](https://github.com/cashapp/sqldelight/) - Local storage
    - [Coroutines Extensions](https://cashapp.github.io/sqldelight/js_sqlite/coroutines/) Consume queries as Flow

### iOS
* [Kingfisher](https://github.com/onevcat/Kingfisher) - Image library.
* [OAuthSwift](https://github.com/OAuthSwift/OAuthSwift) Swift-based OAuth library for iOS and macOS.
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
- [ ] Migrate to Material3
- [ ] Add Episode detail screen
- [ ] Add Watchlist
- [ ] Implement Search

iOS
- [x] Add HomeScreen: Tabs & Empty UI
- [x] Implement Discover UI
- [x] Show Detail Screen
- [x] Add Settings panel.
- [x] Implement trakt auth & sign in
- [ ] Add Seasons UI
- [ ] Implement Search UI
- [ ] Implement Watchlist UI

Shared
- [x] Use SQLDelight extensions to consume queries as Flow
- [x] Refactor interactor implementation.
- [x] Use koin for injection
- [x] Modularize `shared` module
- [x] Try out [Flow-Redux](https://github.com/freeletics/FlowRedux)
- [ ] Improve error handling.
- [ ] Fix paging
- [ ] Add test cases.



### References
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
