plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    api(projects.android.core.traktAuth)
    api(projects.shared.core.datastore.api)
    api(projects.shared.domain.settings)

    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.navigation.common )
    api(libs.androidx.navigation.runtime)
    api(libs.coroutines.core)

    implementation(projects.android.core.resources)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)

}
