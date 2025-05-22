plugins {
  alias(libs.plugins.tvmaniac.android)
}

tvmaniac {
  android {
    useCompose()
    useRoborazzi()
  }

  optIn(
    "androidx.compose.foundation.ExperimentalFoundationApi",
    "androidx.compose.material.ExperimentalMaterialApi",
    "androidx.compose.material3.ExperimentalMaterial3Api",
  )
}

dependencies {
  api(projects.presenter.watchlist)

  implementation(projects.androidDesignsystem)
  implementation(projects.i18n)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.kotlinx.collections)

  testImplementation(projects.core.screenshotTests)
}
