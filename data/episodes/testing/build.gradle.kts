plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.database)
        implementation(projects.data.episodes.api)

        implementation(libs.coroutines.core)
      }
    }
  }
}
