import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets

plugins {
  alias(libs.plugins.tvmaniac.android.library)
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.serialization)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.ktor.serialization)

      implementation(projects.core.base)
      implementation(libs.androidx.paging.common)
      implementation(libs.coroutines.core)
      implementation(libs.bundles.kotlinInject)
      implementation(libs.ktor.core)
      implementation(libs.store5)
    }
  }
}

android { namespace = "com.thomaskioko.tvmaniac.core.networkutil" }

addKspDependencyForAllTargets(libs.kotlinInject.anvil.compiler)
