plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.cast.api)
        implementation(projects.database)

        implementation(libs.coroutines.core)
      }
    }
  }
}
