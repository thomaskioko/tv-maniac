import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets

plugins {
  alias(libs.plugins.tvmaniac.android.library)
  alias(libs.plugins.tvmaniac.multiplatform)
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
        implementation(projects.datastore.api)
        implementation(projects.traktApi.api)

        implementation(libs.ktor.core)
        implementation(libs.ktor.logging)
        implementation(libs.ktor.negotiation)
        implementation(libs.ktor.serialization.json)
        implementation(libs.bundles.kotlinInject)
        implementation(libs.sqldelight.extensions)
      }
    }

    iosMain {
      dependencies {
        implementation(projects.traktApi.api)

        implementation(libs.ktor.darwin)
      }
    }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)

android { namespace = "com.thomaskioko.trakt.api.implementation" }
