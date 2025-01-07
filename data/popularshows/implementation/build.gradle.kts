import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets
import com.thomaskioko.tvmaniac.plugins.addLanguageArgs

plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger)
        implementation(projects.core.paging)
        implementation(projects.core.networkUtil)
        implementation(projects.core.util)
        implementation(projects.data.popularshows.api)
        implementation(projects.data.requestManager.api)
        implementation(projects.database)
        implementation(projects.tmdbApi.api)

        api(libs.coroutines.core)

        implementation(libs.bundles.kotlinInject)
        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}

addLanguageArgs(
  "androidx.paging.ExperimentalPagingApi",
)

addKspDependencyForAllTargets(libs.kotlinInject.anvil.compiler)
