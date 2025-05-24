plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    addAndroidTarget()
    useKotlinInject()
    useKspAnvilCompiler()
  }
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        api(libs.moko.resources.compose)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.i18n.api)

        api(libs.moko.resources)
      }
    }
  }
}
