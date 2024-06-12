plugins {
  alias(libs.plugins.tvmaniac.android.library)
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.ktor.serialization)

      implementation(projects.core.base)
      implementation(libs.androidx.paging.common)
      implementation(libs.coroutines.core)
      implementation(libs.kotlinInject.runtime)
      implementation(libs.ktor.core)
      implementation(libs.store5)
    }
  }
}

android { namespace = "com.thomaskioko.tvmaniac.core.networkutil" }
