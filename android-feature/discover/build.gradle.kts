import com.thomaskioko.tvmaniac.extensions.addCompilerOptInArgs

plugins {
  alias(libs.plugins.tvmaniac.compose.library)
  alias(libs.plugins.roborazzi)
}

android { namespace = "com.thomaskioko.tvmaniac.ui.discover" }

dependencies {
  api(projects.presenter.discover)

  implementation(projects.androidDesignsystem)
  implementation(projects.androidResources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.snapper)

  testImplementation(projects.core.screenshotTests)
  testImplementation(libs.androidx.compose.ui.test)
  testImplementation(libs.robolectric)
  testRuntimeOnly(libs.roborazzi)
}

addCompilerOptInArgs(
    listOf(
        "androidx.compose.foundation.ExperimentalFoundationApi",
        "androidx.compose.material.ExperimentalMaterialApi",
        "androidx.compose.material3.ExperimentalMaterial3Api",
        "dev.chrisbanes.snapper.ExperimentalSnapperApi",
    ),
)
