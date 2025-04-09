plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    addAndroidTarget()
    useKotlinInject()
    useKspAnvilCompiler()
  }
}


kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(projects.traktAuth.api)

        implementation(libs.androidx.activity)
        implementation(libs.androidx.browser)
        implementation(libs.androidx.core.ktx)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger.api)
        implementation(projects.traktAuth.api)
        implementation(libs.bundles.kotlinInject)
      }
    }
  }
}
