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
    "androidx.compose.material3.ExperimentalMaterial3Api",
    "dev.chrisbanes.snapper.ExperimentalSnapperApi",
  )
}

dependencies {
  api(projects.presenter.showDetails)

  implementation(projects.androidDesignsystem)
  implementation(projects.i18n.core)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material.icons)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.coil.compose)
  implementation(libs.snapper)

  testImplementation(projects.core.screenshotTests)
}
