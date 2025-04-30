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
        implementation(projects.data.database.sqldelight)
        implementation(projects.data.search.api)
        implementation(projects.tmdbApi.api)

        implementation(libs.sqldelight.extensions)
        implementation(libs.store5)
      }
    }
  }
}
