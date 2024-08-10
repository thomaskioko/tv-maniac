import com.thomaskioko.tvmaniac.extensions.addCompilerOptInArgs

plugins {
  alias(libs.plugins.tvmaniac.compose.library)
  alias(libs.plugins.roborazzi)
}

android { namespace = "com.thomaskioko.tvmaniac.ui.moreshows" }

dependencies {
  api(projects.presentation.moreShows)

  implementation(projects.android.designsystem)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.paging.compose)

  testImplementation(projects.android.screenshotTests)
  testImplementation(libs.androidx.compose.ui.test)
  testImplementation(libs.robolectric)
  testRuntimeOnly(libs.roborazzi)
}

addCompilerOptInArgs(
  listOf(
    "androidx.compose.material.ExperimentalMaterialApi",
    "androidx.compose.material3.ExperimentalMaterial3Api",
    "androidx.compose.foundation.ExperimentalFoundationApi"
  )
)
