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
        implementation(projects.database)
        implementation(projects.traktApi.api)
        implementation(projects.core.util)
        implementation(projects.data.library.api)

        implementation(libs.kotlinInject.runtime)
        implementation(libs.sqldelight.extensions)
      }
    }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)
