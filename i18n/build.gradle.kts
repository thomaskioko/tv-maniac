plugins {
  alias(libs.plugins.tvmaniac.kmp)
  alias(libs.plugins.moko.resources)
}

tvmaniac {
  multiplatform {
    addAndroidTarget()

    addIosTargetsWithXcFramework(
      frameworkName = "i18n",
    ) { framework ->
      with(framework) {
        isStatic = true

        export(libs.moko.resources)
      }
    }
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
