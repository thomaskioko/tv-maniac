plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useSerialization()
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)
        api(projects.core.networkUtil)
      }
    }
  }
}
