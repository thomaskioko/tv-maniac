plugins {
  id("plugin.tvmaniac.kotlin.android")
  id("plugin.tvmaniac.multiplatform")
  alias(libs.plugins.serialization)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.ktor.serialization)

      implementation(libs.coroutines.core)
      implementation(libs.decompose.decompose)
      implementation(libs.kermit)
      implementation(libs.napier)
      implementation(libs.kotlinx.datetime)
      implementation(libs.kotlinInject.runtime)
      implementation(libs.ktor.core)
      implementation(libs.paging.common)
      implementation(libs.store5)
      implementation(libs.yamlkt)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.bundles.unittest)
    }
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
