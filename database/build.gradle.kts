plugins {
  alias(libs.plugins.tvmaniac.kotlin.android)
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    androidMain { dependencies { implementation(libs.sqldelight.driver.android) } }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(libs.sqldelight.primitive.adapters)
        implementation(libs.kotlinInject.runtime)
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

dependencies {
  add("kspAndroid", libs.kotlinInject.compiler)
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}

android { namespace = "com.thomaskioko.tvmaniac.db" }

sqldelight {
  databases { create("TvManiacDatabase") { packageName.set("com.thomaskioko.tvmaniac.core.db") } }
  linkSqlite.set(true)
}
