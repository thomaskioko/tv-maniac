plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    addAndroidTarget {
      testOptions.unitTests.isIncludeAndroidResources = true
    }
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

    androidUnitTest {
      dependencies {
        implementation(libs.androidx.junit)
        implementation(libs.robolectric)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.i18n.api)

        api(libs.moko.resources)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.bundles.unittest)
      }
    }
  }
}
