plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.datastore.api)
        implementation(libs.coroutines.core)
      }
    }
  }
}
