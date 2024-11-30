import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets

plugins {
  alias(libs.plugins.tvmaniac.android.library)
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    androidMain { dependencies { implementation(libs.ktor.okhttp) } }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger)
        implementation(projects.tmdbApi.api)

        implementation(libs.bundles.kotlinInject)
        implementation(libs.ktor.core)
        implementation(libs.ktor.logging)
        implementation(libs.ktor.negotiation)
        implementation(libs.ktor.serialization.json)
        implementation(libs.sqldelight.extensions)
        implementation(libs.sqldelight.extensions)
      }
    }

    commonTest { dependencies { implementation(libs.ktor.serialization) } }

    iosMain {
      dependencies {
        implementation(libs.ktor.darwin)
        implementation(libs.ktor.negotiation)
      }
    }
  }
}

android { namespace = "com.thomaskioko.tvmaniac.tmdb.implementation" }

addKspDependencyForAllTargets(libs.kotlinInject.compiler)
addKspDependencyForAllTargets(libs.kotlinInject.anvil.compiler)
