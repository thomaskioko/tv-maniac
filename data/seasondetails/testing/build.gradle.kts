plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.data.seasondetails.api)
        implementation(projects.core.database)

        implementation(libs.coroutines.core)
      }
    }
  }
}
