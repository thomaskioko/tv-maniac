plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.ui.home" }

dependencies {
  api(projects.presenter.home)

  implementation(projects.androidDesignsystem)
  implementation(projects.androidResources)

  implementation(projects.androidFeature.discover)
  implementation(projects.androidFeature.watchlist)
  implementation(projects.androidFeature.search)
  implementation(projects.androidFeature.settings)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.decompose.extensions.compose)
}
