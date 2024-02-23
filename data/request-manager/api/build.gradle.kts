plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.core.database)
        api(libs.kotlinx.datetime)
      }
    }
  }
}
