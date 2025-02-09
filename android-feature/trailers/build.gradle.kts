plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.ui.trailers" }

dependencies {
  api(projects.presenter.trailers)

  implementation(projects.androidDesignsystem)
  implementation(projects.androidResources)

  implementation(libs.androidx.compose.constraintlayout)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.youtubePlayer)
}
