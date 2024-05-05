plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)
        api(projects.core.networkUtil)
        api(projects.data.shows.api)

        implementation(projects.core.paging)

        api(libs.paging.common)
        api(libs.coroutines.core)
      }
    }
  }
}
