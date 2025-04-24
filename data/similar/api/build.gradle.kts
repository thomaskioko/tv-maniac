plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.core.networkUtil)

        implementation(projects.database)
        implementation(projects.data.shows.api)

        api(libs.coroutines.core)
      }
    }
  }
}
