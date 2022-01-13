Tv-Maniac 🚧 Under Heavy Development 🚧
-------------------------
Tv-Maniac is a Multiplatform app (Android & iOS) for viewing TV Shows information from
[TMDB](https://www.themoviedb.org/).

### Android - Screenshots

<table>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/HomeScreen.png?raw=true" alt="Home Screen" width="300"/>
    </p>
  </td>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/ShowDetail.png?raw=true" alt="Show Details Screen" width="300"/>
    </p>
  </td>
</tr>
</table>

### iOS - Screenshots

<table>
  <td>
    <p align="center">
      <img src="https://github.com/c0de-wizard/tv-maniac/blob/main/art/iOS-HomeScreen.png?raw=true" alt="Home Screen" width="300"/>
    </p>
  </td>
</tr>
</table>

## Project Setup & Environment

### TMDB Api
- Create `local.properties` in `root` dir
- Add the following
    ```
    TMDB_API_URL=https://api.themoviedb.org/3/
    ```
- Run `./gradlew generateBuildKonfig`


### Android
- Java 11
- You require the latest [Android Studio BumbleBee](https://androidstudio.googleblog.com/2021/05/android-studio-bumblebee-canary-1.html) release to be able to build the app.
- Install Kmm Plugin. Checkout [this setup guide](https://kotlinlang.org/docs/kmm-setup.html).


### Opening iOS Project
- Navigate to ios directory & open `.xcworkspace` & not `.xcodeproj`


## Project Structure & Architecture
Tvmainic has 3 main directories. I'll break down each module/directory below.

1. **app**: Anrdoid Code
2. **ios**: iOS Workspace
3. **shared**: Kotlin multiplatform code.

	
* **app**: This contains the entry point to the android app. `MainActivity`. There are various modules that are used across the app.
	* **app-common**: This contaions common code used in features.
		* **compose**: Common jetpack compose components and code used by every feature. eg `theme`, `colors`, `ui-components` e.t.c
		* **navigation**: Core navigation logic
		* **annotation**: Coroutine scope and dispatcher annotation used in feature modules.
	* **app-feature**: This `module` contains feature module of the Android app. Each feature has 3 main classes
		* `FeatureNavigationFactory`: Allows us to add the screen to the navGraph
		* `ComposableScreen`: UI Screen built using compose
		* `Viewmodule`: Allows us to manage UI related data 
* **ios**: This directory contains the iOS workspace.
* **shared**: This is where the realm of the shared logic. It contains both iOS and Android implementation. I also used a modular architecture in the shared module. This prevents us from having a huge shared module. It also becomes easy to modify and add more feature. Here's an overview of how shared is modularised.
	* **core**: Contains common classes & functions or utility classes that are used but other modules.
	* **core-test**: Contains test util classes. We then add this module to each module that has tests.
	* **database**: Contains [SQLDelight](https://github.com/cashapp/sqldelight/) implementation & Sql files. 
	* **remote**: Contails [Ktor](https://ktor.io/) implementation
	* **domain**: This module uses a feature like approach. So we can have `domain-discover` with has two modules:
		* `api`: Contains domain interfaces/abstract classes. This module is also exported for iOS and what android feature modules depend on.
		* `implementation`: Contains domain implementation logic e.g fetch & cache data.
	* **interactors**: This is more of a limbo module of interactor classes that don't have complete features. I didn't want to have them in the Android feature modules that why this exists. As we improve on the app, we will move these classes to the domain module and get rid of this module.

	
### Dependency Injection

TvManiac uses two different dependencies [Dagger Hilt](https://dagger.dev/hilt) for Android and [koin](https://github.com/mockk/mockk) for the shared module. Using Koin in the shared module allows us to provide dependencies in the iOS app.
	   
I'll keep updating & changing things as I learn. 🤓


## Libraries Used
### Android
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
    * [Coil](https://coil-kt.github.io/coil/compose/) - Image loading
    * [Navigation](https://developer.android.com/jetpack/compose/navigation) - Navigation
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) UI related data holder, lifecycle
  aware.
* [Accompanist](https://github.com/google/accompanist)
    * [Insets](https://google.github.io/accompanist/insets/)
    * [Pager Layouts](https://google.github.io/accompanist/pager/)
* [Dagger Hilt](https://dagger.dev/hilt) - dependency injection.
* [KenBurnsView](https://github.com/flavioarfaria/KenBurnsView) - Immersive image.
* [Leakcanary](https://github.com/square/leakcanary) - Memory leak detection.

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

### iOS (Coming Soon)

## Roadmap
Android
- [x] Implement Watchlist
- [x] Add `More` screen. Shows GridView
- [ ] Implement Search
- [ ] Recommended Shows
- [x] Implement pagination.
- [x] Add Settings panel.
    - Dynamic theme change.
- [ ] Observe Internet connection

iOS
- [x] Add HomeScreen: Tabs & Empty UI
- [x] Implement Discover UI
- [ ] Show Detail Screen
- [ ] Implement Search UI
- [ ] Implement Watchlist UI
- [ ] Implement Load more

Shared
- [x] Use SQLDelight extensions to consume queries as Flow
- [x] Refactor interactor implementation.
- [x] Use koin for injection
- [x] Modularize `shared` module
- [ ] Better MVI implementation
  
    [ ] Have `shared-core` module have most of the implementation.
    [ ] Improve error state, add retry.


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
