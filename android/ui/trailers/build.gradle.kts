plugins { id("plugin.tvmaniac.compose.library") }

android { namespace = "com.thomaskioko.tvmaniac.ui.trailers" }

dependencies {
  api(projects.presentation.trailers)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(libs.androidx.compose.constraintlayout)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.decompose.extensions.compose)
  implementation(libs.youtubePlayer)
}
