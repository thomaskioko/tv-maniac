import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets
import com.thomaskioko.tvmaniac.plugins.addLanguageArgs

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
      implementation(projects.core.logger)
      implementation(libs.coroutines.core)
      implementation(libs.kermit)
      implementation(libs.kotlinx.datetime)
      implementation(libs.bundles.kotlinInject)
      implementation(libs.ktor.core)
      implementation(libs.yamlkt)
    }

    commonTest.dependencies { implementation(libs.bundles.unittest) }
  }
}

android {
  namespace = "com.thomaskioko.tvmaniac.util"

  sourceSets["main"].apply {
    resources.srcDirs("src/commonMain/resources") // <-- add the commonMain Resources
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)

addLanguageArgs(
  "kotlinx.coroutines.ExperimentalCoroutinesApi",
  "kotlinx.cinterop.ExperimentalForeignApi",
  "kotlinx.cinterop.BetaInteropApi",
)
