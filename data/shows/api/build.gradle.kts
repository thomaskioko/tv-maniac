plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.database)

        api(libs.coroutines.core)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.database.test)

        implementation(libs.kotest.assertions)
        implementation(libs.kotlin.test)
      }
    }
  }
}
