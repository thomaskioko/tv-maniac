import com.thomaskioko.tvmaniac.extensions.TvManiacBuildType

plugins {
  id("tvmaniac.application")
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
  implementation(projects.android.feature.discover)
  implementation(projects.android.feature.library)
  implementation(projects.android.feature.moreShows)
  implementation(projects.android.feature.search)
  implementation(projects.android.feature.seasonDetails)
  implementation(projects.android.feature.settings)
  implementation(projects.android.feature.showDetails)
  implementation(projects.android.feature.trailers)
  implementation(projects.shared)
  implementation(projects.core.base)
  implementation(projects.core.util)
  implementation(projects.traktAuth.api)
  implementation(projects.traktAuth.implementation)
  implementation(projects.navigation)

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
