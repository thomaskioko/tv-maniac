plugins {
  alias(libs.plugins.tvmaniac.android)
}

tvmaniac {
  android {
    useCompose()
  }

  optIn(
    "androidx.compose.material3.ExperimentalMaterial3Api",
  )
}

dependencies {
  api(projects.presenter.settings)
  api(projects.datastore.api)

  implementation(projects.androidDesignsystem)
  implementation(projects.androidResources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.coil.compose)
}
