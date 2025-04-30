plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.seasondetails.api)
        api(projects.data.database.sqldelight)

        implementation(libs.coroutines.core)
      }
    }
  }
}
