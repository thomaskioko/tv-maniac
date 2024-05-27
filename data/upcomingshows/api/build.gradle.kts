plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)
        api(projects.core.networkUtil)
        api(projects.data.shows.api)

        api(libs.paging.common)
        api(libs.coroutines.core)
      }
    }
  }
}
