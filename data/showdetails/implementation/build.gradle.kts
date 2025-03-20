plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
    useKspAnvilCompiler()
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.database)
        implementation(projects.data.showdetails.api)
        implementation(projects.tmdbApi.api)
        implementation(projects.core.util)
        implementation(projects.data.cast.api)
        implementation(projects.data.seasons.api)
        implementation(projects.data.trailers.api)
        implementation(projects.data.requestManager.api)

        api(libs.coroutines.core)

        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }
  }
}
