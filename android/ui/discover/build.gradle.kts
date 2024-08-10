import com.thomaskioko.tvmaniac.extensions.addCompilerArgs

plugins {
  alias(libs.plugins.tvmaniac.compose.library)
  alias(libs.plugins.roborazzi)
}

android { namespace = "com.thomaskioko.tvmaniac.ui.discover" }

dependencies {
  api(projects.presentation.discover)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.ui.util)
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
        "androidx.compose.material.ExperimentalMaterialApi",
        "dev.chrisbanes.snapper.ExperimentalSnapperApi",
    ),
)
