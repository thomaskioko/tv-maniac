plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.topratedshows.api)
        implementation(projects.database)

        implementation(libs.coroutines.core)
      }
    }
  }
}
