plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)

        api(libs.coroutines.core)
      }
    }
  }
}
