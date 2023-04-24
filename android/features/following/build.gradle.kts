plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    api(projects.shared.domain.following)

    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.coroutines.core)

    implementation(projects.android.core.resources)

    implementation(libs.androidx.compose.material3)
}
