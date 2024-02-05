plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.database)
        implementation(projects.core.util)
        api(projects.data.seasons.api)

        implementation(libs.coroutines.core)
      }
    }
  }
}
