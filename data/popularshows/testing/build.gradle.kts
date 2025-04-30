plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.popularshows.api)
        implementation(projects.data.database.sqldelight)

        implementation(libs.coroutines.core)
      }
    }
  }
}
