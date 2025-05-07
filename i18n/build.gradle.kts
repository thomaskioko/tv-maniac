plugins {
  alias(libs.plugins.tvmaniac.kmp)
  alias(libs.plugins.moko.resources)
}

tvmaniac {
  multiplatform {
    addAndroidTarget()
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
        api(libs.moko.resources)
      }
    }
  }
}

multiplatformResources {
  resourcesPackage.set("com.thomaskioko.tvmaniac.i18n")
}
