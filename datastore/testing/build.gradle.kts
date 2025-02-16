plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.datastore.api)
        implementation(libs.coroutines.core)
      }
    }
  }
}
