plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    addAndroidTarget()
    useKotlinInject()
    useKspAnvilCompiler()
    useSerialization()
  }

  optIn(
    "kotlinx.coroutines.InternalCoroutinesApi",
  )
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.core.view)

      implementation(libs.coroutines.core)
      implementation(libs.decompose.decompose)
      implementation(libs.bundles.kotlinInject)
    }
  }
}
