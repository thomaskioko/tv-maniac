plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
    useKspAnvilCompiler()
  }

  optIn(
    "androidx.paging.ExperimentalPagingApi",
  )
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger.api)
        implementation(projects.core.networkUtil)
        implementation(projects.core.paging)
        implementation(projects.data.database.sqldelight)
        implementation(projects.data.topratedshows.api)
        implementation(projects.tmdbApi.api)
        implementation(projects.core.util)
        implementation(projects.data.requestManager.api)

        api(libs.coroutines.core)

        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }
  }
}
