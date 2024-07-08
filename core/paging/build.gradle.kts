plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.data.shows.api)
      implementation(libs.coroutines.core)
      implementation(libs.androidx.paging.common)
      implementation(libs.sqldelight.primitive.adapters)
    }
  }
}
