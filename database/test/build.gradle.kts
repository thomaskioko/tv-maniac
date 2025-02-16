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
    androidMain { dependencies { implementation(libs.sqldelight.driver.jvm) } }

    commonMain {
      dependencies {
        implementation(projects.database)
        implementation(libs.kotlinx.datetime)

        implementation(libs.kotlin.test)
      }
    }

    iosMain { dependencies { implementation(libs.sqldelight.driver.native) } }

    jvmMain { dependencies { implementation(libs.sqldelight.driver.jvm) } }
  }
}
