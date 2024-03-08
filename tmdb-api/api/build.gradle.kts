plugins {
  id("plugin.tvmaniac.multiplatform")
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)
        api(projects.core.networkUtil)

        implementation(libs.ktor.serialization)
      }
    }
  }
}
