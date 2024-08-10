import com.thomaskioko.tvmaniac.extensions.addCompilerArgs

plugins {
  alias(libs.plugins.tvmaniac.compose.library)
  alias(libs.plugins.roborazzi)
}

android { namespace = "com.thomaskioko.tvmaniac.ui.showdetails" }

dependencies {
  api(projects.presentation.showDetails)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material.icons)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.snapper)

  testImplementation(projects.android.screenshotTests)
  testImplementation(libs.androidx.compose.ui.test)
  testImplementation(libs.robolectric)
  testRuntimeOnly(libs.roborazzi)
}

addCompilerArgs(
  listOf(
    "androidx.compose.foundation.ExperimentalFoundationApi",
    "androidx.compose.material3.ExperimentalMaterial3Api",
    "dev.chrisbanes.snapper.ExperimentalSnapperApi"
  )
)
