plugins {
  alias(libs.plugins.tvmaniac.kmp)
  alias(libs.plugins.sqldelight)
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
    androidMain { dependencies { implementation(libs.sqldelight.driver.android) } }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(libs.sqldelight.primitive.adapters)
        implementation(libs.bundles.kotlinInject)
        implementation(libs.kotlinx.datetime)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.database.test)

        implementation(libs.kotest.assertions)
        implementation(libs.kotlin.test)
      }
    }

    iosMain { dependencies { implementation(libs.sqldelight.driver.native) } }
  }
}

sqldelight {
  databases {
    create("TvManiacDatabase"){
      packageName.set("com.thomaskioko.tvmaniac.db")

      schemaOutputDirectory.set(file("src/commonMain/sqldelight/com/thomaskioko/tvmaniac/schemas"))
    }
  }
}
