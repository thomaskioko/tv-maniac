import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets
import com.thomaskioko.tvmaniac.plugins.addLanguageArgs

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

        implementation(libs.bundles.kotlinInject)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)
addKspDependencyForAllTargets(libs.kotlinInject.anvil.compiler)

android { namespace = "com.thomaskioko.tvmaniac.shared.domain.datastore.implementation" }

addLanguageArgs(
  "kotlinx.coroutines.ExperimentalCoroutinesApi",
  "kotlinx.coroutines.InternalCoroutinesApi",
  "kotlinx.cinterop.ExperimentalForeignApi"
)
