plugins { alias(libs.plugins.tvmaniac.multiplatform) }

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
