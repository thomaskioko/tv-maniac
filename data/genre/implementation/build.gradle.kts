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
        api(libs.coroutines.core)

        implementation(projects.core.base)
        implementation(projects.core.networkUtil)
        implementation(projects.core.util)
        implementation(projects.database)
        implementation(projects.data.genre.api)
        implementation(projects.tmdbApi.api)

        implementation(libs.sqldelight.extensions)
        implementation(libs.store5)
      }
    }
  }
}
