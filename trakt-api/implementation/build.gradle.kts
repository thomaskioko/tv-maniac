plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    addAndroidTarget()
    useKotlinInject()
    useKspAnvilCompiler()
    useSerialization()

  }
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.appauth)
        implementation(libs.ktor.okhttp)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger)
        implementation(projects.datastore.api)
        implementation(projects.traktApi.api)

        implementation(libs.ktor.core)
        implementation(libs.ktor.logging)
        implementation(libs.ktor.negotiation)
        implementation(libs.ktor.serialization.json)
        implementation(libs.sqldelight.extensions)
      }
    }

    iosMain {
      dependencies {
        implementation(projects.traktApi.api)

        implementation(libs.ktor.darwin)
      }
    }
  }
}
