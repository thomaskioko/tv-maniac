plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.showdetails"
}

dependencies {
    api(projects.shared.domain.showDetails)

    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.coroutines.core)

    implementation(projects.android.core.resources)

    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.snapper)
}
