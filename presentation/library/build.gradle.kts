plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.library.api)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)
        api(libs.kotlinx.collections)

        implementation(libs.kotlinInject.runtime)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.data.library.testing)

        implementation(libs.bundles.unittest)
      }
    }
  }
}
