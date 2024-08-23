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
        implementation(projects.database)
        implementation(projects.data.shows.api)
        implementation(projects.data.requestManager.api)

        api(libs.coroutines.core)

        implementation(libs.kotlinInject.runtime)
        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)
