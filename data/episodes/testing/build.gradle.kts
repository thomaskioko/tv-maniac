plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.database)
        implementation(projects.data.episodes.api)

        implementation(libs.coroutines.core)
      }
    }
  }
}
