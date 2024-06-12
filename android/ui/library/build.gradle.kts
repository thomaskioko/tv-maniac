plugins {
  alias(libs.plugins.tvmaniac.compose.library)
  alias(libs.plugins.roborazzi)
}

android { namespace = "com.thomaskioko.tvmaniac.ui.library" }

dependencies {
  api(projects.presentation.library)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.decompose.extensions.compose)
  implementation(libs.kotlinx.collections)

  testImplementation(projects.android.screenshotTests)
  testImplementation(libs.robolectric)
  testDemoImplementation(libs.roborazzi)
}
