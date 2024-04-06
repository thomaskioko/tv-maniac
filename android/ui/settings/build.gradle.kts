plugins { id("plugin.tvmaniac.compose.library") }

android { namespace = "com.thomaskioko.tvmaniac.ui.settings" }

dependencies {
  api(projects.presentation.settings)

  implementation(projects.datastore.api)
  implementation(projects.data.shows.api)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.decompose.extensions.compose)
  implementation(libs.kotlinx.collections)
}
