plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.database)
        api(projects.data.seasons.api)

        implementation(libs.coroutines.core)
      }
    }
  }
}
