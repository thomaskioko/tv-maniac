plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.shows.api)
        implementation(libs.coroutines.core)
      }
    }
  }
}
