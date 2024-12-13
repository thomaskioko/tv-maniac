import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets

plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.coroutines.core)

        implementation(projects.core.base)
        implementation(projects.core.networkUtil)
        implementation(projects.core.util)
        implementation(projects.database)
        implementation(projects.data.genre.api)
        implementation(projects.tmdbApi.api)

        implementation(libs.bundles.kotlinInject)
        implementation(libs.sqldelight.extensions)
        implementation(libs.store5)
      }
    }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)
addKspDependencyForAllTargets(libs.kotlinInject.anvil.compiler)
