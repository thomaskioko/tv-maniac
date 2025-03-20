plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.watchlist.api)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)
        api(libs.kotlinx.collections)

      }
    }

    commonTest {
      dependencies {
        implementation(projects.data.watchlist.testing)

        implementation(libs.bundles.unittest)
      }
    }
  }
}
