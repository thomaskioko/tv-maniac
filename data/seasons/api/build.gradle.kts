plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.core.database)
        api(projects.core.networkUtil)

        api(libs.coroutines.core)
      }
    }
  }
}
