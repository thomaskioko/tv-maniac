plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.popularshows.api)
        implementation(projects.database)

        implementation(libs.coroutines.core)
      }
    }
  }
}
