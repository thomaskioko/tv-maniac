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
        implementation(projects.core.base)
        implementation(projects.database)
        implementation(projects.data.cast.api)

        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.database.test)

        implementation(libs.bundles.unittest)
      }
    }
  }
}

