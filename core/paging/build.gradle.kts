plugins {
  id("plugin.tvmaniac.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.coroutines.core)
      implementation(libs.paging.common)
    }
  }
}
