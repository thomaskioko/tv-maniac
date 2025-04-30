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
        implementation(projects.data.database.sqldelight)
        api(libs.kotlinx.datetime)
      }
    }
  }
}
