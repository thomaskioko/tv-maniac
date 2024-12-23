plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.ui.home" }

dependencies {
  api(projects.presenter.home)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(projects.android.ui.discover)
  implementation(projects.android.ui.watchlist)
  implementation(projects.android.ui.search)
  implementation(projects.android.ui.settings)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.decompose.extensions.compose)
}
