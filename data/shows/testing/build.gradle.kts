plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.shows.api)
        implementation(libs.coroutines.core)
      }
    }
  }
}
