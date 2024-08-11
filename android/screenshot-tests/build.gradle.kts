import com.thomaskioko.tvmaniac.extensions.addCompilerOptInArgs

plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.screenshottesting" }

dependencies {
  implementation(projects.android.designsystem)

  implementation(libs.androidx.compose.ui.test)
  implementation(libs.robolectric)
  implementation(libs.roborazzi)

  runtimeOnly(libs.androidx.compose.ui.test.manifest)
}

addCompilerOptInArgs(
  listOf("com.github.takahirom.roborazzi.ExperimentalRoborazziApi")
)
