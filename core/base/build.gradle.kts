plugins {
  alias(libs.plugins.tvmaniac.android.library)
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.coroutines.core)
      implementation(libs.decompose.decompose)
      implementation(libs.kotlinInject.runtime)
      implementation(libs.ktor.serialization)
    }
  }
}

android { namespace = "com.thomaskioko.tvmaniac.core.base" }
