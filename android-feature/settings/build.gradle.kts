import com.thomaskioko.tvmaniac.extensions.addCompilerOptInArgs

plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.ui.settings" }

dependencies {
  api(projects.presenter.settings)
  api(projects.datastore.api)

  implementation(projects.androidDesignsystem)
  implementation(projects.androidResources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
}

addCompilerOptInArgs(
  listOf(
    "androidx.compose.material3.ExperimentalMaterial3Api",
  )
)
