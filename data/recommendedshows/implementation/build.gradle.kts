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
        implementation(projects.core.paging)
        implementation(projects.core.logger)
        implementation(projects.core.util)
        implementation(projects.data.recommendedshows.api)
        implementation(projects.data.requestManager.api)
        implementation(projects.data.shows.api)
        implementation(projects.tmdbApi.api)

        implementation(libs.bundles.kotlinInject)
        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.anvil.compiler)
