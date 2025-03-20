plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    addAndroidTarget(
      androidConfig = {
        sourceSets["main"].resources.setSrcDirs(listOf("src/commonMain/resources"))
      }
    )
    useKotlinInject()
    useKspAnvilCompiler()
    useSerialization()
  }

  optIn(
    "kotlinx.coroutines.ExperimentalCoroutinesApi",
    "kotlinx.cinterop.ExperimentalForeignApi",
    "kotlinx.cinterop.BetaInteropApi",
  )
}

kotlin {
  sourceSets {
    commonMain.dependencies {
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
