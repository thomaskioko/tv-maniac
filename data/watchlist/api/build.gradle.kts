plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)
        api(projects.core.networkUtil)

        api(libs.coroutines.core)
      }
    }
  }
}
