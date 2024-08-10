import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets

plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.episodes.api)
        implementation(projects.data.shows.api)
        implementation(projects.tmdbApi.api)

        implementation(libs.kotlinInject.runtime)
        implementation(libs.sqldelight.extensions)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)
