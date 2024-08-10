import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets

plugins {
  alias(libs.plugins.tvmaniac.android.library)
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.datastore.api)

        api(libs.androidx.datastore.preference)

        implementation(libs.kotlinInject.runtime)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)

android { namespace = "com.thomaskioko.tvmaniac.shared.domain.datastore.implementation" }
