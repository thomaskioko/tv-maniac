plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.datastore.api)
        implementation(projects.traktAuth.api)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)

        implementation(libs.bundles.kotlinInject)
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
