import com.thomaskioko.tvmaniac.extensions.TvManiacBuildType

plugins {
  alias(libs.plugins.tvmaniac.application)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.thomaskioko.tvmaniac"

  defaultConfig {
    applicationId = "com.thomaskioko.tvmaniac"
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes { debug { applicationIdSuffix = TvManiacBuildType.DEBUG.applicationIdSuffix } }

  packaging {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
      excludes.add("/META-INF/versions/9/previous-compilation-data.bin")
    }
  }
}

dependencies {
  implementation(projects.android.designsystem)
  implementation(projects.android.ui.discover)
  implementation(projects.android.ui.library)
  implementation(projects.android.ui.moreShows)
  implementation(projects.android.ui.search)
  implementation(projects.android.ui.seasonDetails)
  implementation(projects.android.ui.settings)
  implementation(projects.android.ui.showDetails)
  implementation(projects.android.ui.trailers)
  implementation(projects.shared)
  implementation(projects.core.base)
  implementation(projects.core.util)
  implementation(projects.traktAuth.api)
  implementation(projects.traktAuth.implementation)
  implementation(projects.navigation.api)
  implementation(projects.navigation.implementation)

  implementation(libs.androidx.compose.activity)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons)
  implementation(libs.androidx.compose.ui.util)
  implementation(libs.appauth)

  implementation(libs.decompose.decompose)
  implementation(libs.decompose.extensions.compose)
  implementation(libs.kotlinInject.runtime)
  ksp(libs.kotlinInject.compiler)
}

ksp { arg("me.tatarka.inject.generateCompanionExtensions", "true") }
