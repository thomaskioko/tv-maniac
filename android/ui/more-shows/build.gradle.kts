plugins {
  alias(libs.plugins.tvmaniac.compose.library)
  alias(libs.plugins.roborazzi)
}

android { namespace = "com.thomaskioko.tvmaniac.ui.moreshows" }

dependencies {
  api(projects.presentation.moreShows)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.decompose.extensions.compose)
  implementation(libs.androidx.paging.compose)

  testImplementation(projects.android.screenshotTests)
  testImplementation(libs.androidx.compose.ui.test)
  testImplementation(libs.robolectric)
  testDemoImplementation(libs.roborazzi)
}
