plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)
        api(libs.kotlinx.datetime)
      }
    }
  }
}
