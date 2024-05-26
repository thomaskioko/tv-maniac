plugins {
  id("plugin.tvmaniac.compose.library")
  alias(libs.plugins.roborazzi)
}

android { namespace = "com.thomaskioko.tvmaniac.ui.seasondetails" }

dependencies {
  api(projects.presentation.seasondetails)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(libs.androidx.compose.constraintlayout)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.decompose.extensions.compose)
  implementation(libs.snapper)

  testImplementation(projects.android.screenshotTests)
  testImplementation(libs.androidx.compose.ui.test)
  testImplementation(libs.robolectric)
  testDemoImplementation(libs.roborazzi)
}
