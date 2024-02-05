plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.util)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)
        implementation(libs.kotlinInject.runtime)
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))

        implementation(libs.bundles.unittest)
      }
    }
  }
}
