Tv-Maniac 🚧 Under Heavy Development 🚧
-------------------------
Tv-Maniac is a Multiplatform app (Android & iOS) for viewing TV Shows from
[TMDB](https://www.themoviedb.org/).

## Libraries Used
### Android
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
    * [Coil](https://coil-kt.github.io/coil/compose/) - Image loading
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) UI related data holder, lifecycle
  aware.
* [Accompanist](https://github.com/google/accompanist)
    * [Pager Composable](https://github.com/google/accompanist/tree/main/pager)
* [Dagger Hilt](https://dagger.dev/hilt) - dependency injection.

### Common
* [Ktor](https://ktor.io/) - Networking
* [Kotlinx Serialization](https://ktor.io/docs/kotlin-serialization.html) - De/Serializing JSON
* [Coroutines](https://github.com/Kotlin/kotlinx.coroutines#multiplatform) - Concurrency & Threading
* [SQLDelight](https://github.com/cashapp/sqldelight/) - Local storage
* [Napier](https://github.com/AAkira/Napier) - Logging
* [Mockk](https://github.com/mockk/mockk) - mocking library for Kotlin.