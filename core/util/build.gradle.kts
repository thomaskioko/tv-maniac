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
      implementation(libs.kotlinInject.runtime)
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

  compileOptions {
    // Flag to enable support for the new language APIs
    isCoreLibraryDesugaringEnabled = true
  }

  defaultConfig { multiDexEnabled = true }
}

dependencies {
  add("coreLibraryDesugaring", libs.android.desugarJdkLibs)
  add("kspAndroid", libs.kotlinInject.compiler)
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}
