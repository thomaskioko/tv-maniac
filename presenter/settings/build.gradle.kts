plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
  }

  optIn(
    "kotlinx.coroutines.ExperimentalCoroutinesApi"
  )
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.datastore.api)
        implementation(projects.traktAuth.api)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)

      }
    }

    commonTest {
      dependencies {
        implementation(projects.datastore.testing)
        implementation(projects.traktAuth.testing)

        implementation(libs.bundles.unittest)
      }
    }
  }
}
