import com.thomaskioko.tvmaniac.extensions.addCompilerOptInArgs

plugins {
  alias(libs.plugins.tvmaniac.compose.library)
  alias(libs.plugins.roborazzi)
}

android { namespace = "com.thomaskioko.tvmaniac.ui.seasondetails" }

dependencies {
  api(projects.presenter.seasondetails)

  implementation(projects.androidDesignsystem)
  implementation(projects.androidResources)

  implementation(libs.androidx.compose.constraintlayout)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.snapper)

  testImplementation(projects.core.screenshotTests)
  testImplementation(libs.androidx.compose.ui.test)
  testImplementation(libs.robolectric)
  testRuntimeOnly(libs.roborazzi)
}

addCompilerOptInArgs(
  listOf(
    "androidx.compose.material.ExperimentalMaterialApi",
    "androidx.compose.material3.ExperimentalMaterial3Api",
    "androidx.compose.foundation.ExperimentalFoundationApi",
    "dev.chrisbanes.snapper.ExperimentalSnapperApi"
  )
)

