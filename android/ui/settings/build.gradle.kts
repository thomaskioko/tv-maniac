import com.thomaskioko.tvmaniac.extensions.addCompilerArgs

plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.ui.settings" }

dependencies {
  api(projects.presentation.settings)
  api(projects.datastore.api)

  implementation(projects.android.designsystem)
  implementation(projects.android.resources)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
}

addCompilerArgs(
  listOf(
    "androidx.compose.material3.ExperimentalMaterial3Api",
  )
)
