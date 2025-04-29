plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.genre.api)
        implementation(projects.data.database.sqldelight)

        implementation(libs.coroutines.core)
      }
    }
  }
}
