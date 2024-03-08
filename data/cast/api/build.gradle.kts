plugins { id("plugin.tvmaniac.multiplatform") }

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
