plugins {
  alias(libs.plugins.tvmaniac.kotlin.android)
  alias(libs.plugins.tvmaniac.multiplatform)
}

kotlin {
  sourceSets {
    androidMain { dependencies { implementation(libs.sqldelight.driver.jvm) } }

    commonMain {
      dependencies {
        implementation(projects.database)

        implementation(libs.kotlin.test)
      }
    }

    iosMain { dependencies { implementation(libs.sqldelight.driver.native) } }

    jvmMain { dependencies { implementation(libs.sqldelight.driver.jvm) } }
  }
}

android { namespace = "com.thomaskioko.tvmaniac.database.test" }
