plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.data.database.sqldelight)

        api(libs.coroutines.core)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.data.database.test)

        implementation(libs.kotest.assertions)
        implementation(libs.kotlin.test)
      }
    }
  }
}
