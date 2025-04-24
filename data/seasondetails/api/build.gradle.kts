plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.database)
        api(projects.core.networkUtil)

        api(libs.coroutines.core)
      }
    }
  }
}
