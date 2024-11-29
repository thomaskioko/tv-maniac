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
        implementation(projects.database)
        implementation(projects.data.trendingshows.api)
        implementation(projects.tmdbApi.api)
        implementation(projects.core.util)
        implementation(projects.data.requestManager.api)

        api(libs.coroutines.core)

        implementation(libs.bundles.kotlinInject)
        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }
  }
}

addLanguageArgs(
  "androidx.paging.ExperimentalPagingApi",
)

addKspDependencyForAllTargets(libs.kotlinInject.compiler)
addKspDependencyForAllTargets(libs.kotlinInject.anvil.compiler)
