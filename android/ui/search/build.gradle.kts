import com.thomaskioko.tvmaniac.extensions.addCompilerArgs

plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.ui.search" }

dependencies {
  api(projects.presentation.search)

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
