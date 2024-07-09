plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.screenshottesting" }

dependencies {
  implementation(projects.android.designsystem)

  api(libs.roborazzi)
  implementation(libs.androidx.compose.activity)
  implementation(libs.androidx.compose.ui.test)
  implementation(libs.robolectric)
}
