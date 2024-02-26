plugins {
  id("plugin.tvmaniac.kotlin.android")
  id("plugin.tvmaniac.multiplatform")
  alias(libs.plugins.serialization)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.appauth)
        implementation(libs.ktor.okhttp)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger)
        implementation(projects.core.traktApi.api)
        implementation(projects.core.datastore.api)

        implementation(libs.ktor.core)
        implementation(libs.ktor.logging)
        implementation(libs.ktor.negotiation)
        implementation(libs.ktor.serialization.json)
        implementation(libs.kotlinInject.runtime)
        implementation(libs.sqldelight.extensions)
      }
    }

    iosMain {
      dependencies {
        implementation(projects.core.traktApi.api)

        implementation(libs.ktor.darwin)
      }
    }
  }
}

dependencies {
  add("kspAndroid", libs.kotlinInject.compiler)
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}

android { namespace = "com.thomaskioko.trakt.api.implementation" }
