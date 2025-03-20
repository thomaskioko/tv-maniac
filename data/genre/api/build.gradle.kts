plugins {
  alias(libs.plugins.tvmaniac.kmp)
}


kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.core.networkUtil)
        api(projects.database)
        api(projects.data.shows.api)

        api(libs.coroutines.core)
      }
    }
  }
}
