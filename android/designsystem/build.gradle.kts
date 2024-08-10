import com.thomaskioko.tvmaniac.extensions.addCompilerOptInArgs

plugins { alias(libs.plugins.tvmaniac.compose.library) }

android { namespace = "com.thomaskioko.tvmaniac.compose" }

dependencies {
  api(libs.androidx.compose.ui.tooling)
  api(libs.androidx.compose.material3)
  api(libs.androidx.compose.ui.ui)
  api(libs.androidx.compose.material.icons)
  api(libs.androidx.compose.runtime)

  implementation(projects.android.resources)

  implementation(libs.androidx.annotation)
  implementation(libs.androidx.collections)
  implementation(libs.androidx.core)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.coil.coil)
  implementation(libs.coil.compose)
  implementation(libs.kenburns)
  implementation(libs.androidx.palette)
  implementation(libs.coroutines.jvm)
}

addCompilerOptInArgs(
  listOf(
    "androidx.compose.material3.ExperimentalMaterial3Api"
  )
)
