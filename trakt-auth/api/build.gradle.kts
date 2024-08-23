plugins {
  alias(libs.plugins.tvmaniac.android.library)
  alias(libs.plugins.tvmaniac.multiplatform)
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        api(libs.appauth)
        api(libs.coroutines.core)
      }
    }

    commonMain {
      dependencies {
        api(projects.datastore.api)
        api(libs.coroutines.core)
      }
    }
  }
}

android { namespace = "com.thomaskioko.tvmaniac.traktauth.api" }
