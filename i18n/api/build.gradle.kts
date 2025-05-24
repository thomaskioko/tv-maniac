plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    addAndroidTarget()
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.i18n.generator)
      }
    }
  }
}
