Tv-Maniac 🚧 Under Heavy Development 🚧
-------------------------
Tv-Maniac is a Multiplatform app (Android & iOS) for viewing TV Shows information from
[TMDB](https://www.themoviedb.org/).

## Tv Maniac

![Tv Maniac Demo](https://github.com/c0de-wizard/tv-maniac/blob/main/art/Tv-Maniac.mp4)

## Environment
- Java 11
- You require the latest [Android Studio BumbleBee](https://androidstudio.googleblog.com/2021/05/android-studio-bumblebee-canary-1.html) release to be able to build the app.

### TMDB Api
- Create `local.properties` in `root` dir
- Add the following
    ```
    TMDB_API_KEY=PUT_API_KEY
    TMDB_API_URL=https://api.themoviedb.org/3/
    ```
- Run `./gradlew generateBuildKonfig`

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

### iOS (Coming Soon)

### Kmp - Common
* [Ktor](https://ktor.io/) - Networking
* [Kotlinx Serialization](https://ktor.io/docs/kotlin-serialization.html) - De/Serializing JSON
* [Coroutines](https://github.com/Kotlin/kotlinx.coroutines#multiplatform) - Concurrency & Threading
* [DateTime](https://github.com/Kotlin/kotlinx-datetime) - Date & Time
* [SQLDelight](https://github.com/cashapp/sqldelight/) - Local storage
* [Napier](https://github.com/AAkira/Napier) - Logging
* [Mockk](https://github.com/mockk/mockk) - mocking library for Kotlin.


### References
- [Design Inspiration](https://dribbble.com/shots/7591814-HBO-Max-Companion-App-Animation)
- [Code Snippets](https://github.com/android/compose-samples)

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
