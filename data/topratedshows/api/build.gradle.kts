plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.core.database)
        api(projects.core.util)
        api(projects.data.shows.api)

        api(libs.paging.common)
        api(libs.coroutines.core)
      }
    }
  }
}
