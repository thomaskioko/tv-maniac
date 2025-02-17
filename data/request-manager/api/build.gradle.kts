plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
    useKspAnvilCompiler()
  }

  optIn(
    "kotlinx.coroutines.ExperimentalCoroutinesApi",
  )
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)
        api(libs.kotlinx.datetime)
      }
    }
  }
}
