plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    api(projects.android.core.traktAuth)
    api(projects.shared.data.profile.api)

    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.coroutines.core)

    implementation(projects.android.core.resources)
    implementation(projects.shared.core.database)
    implementation(projects.shared.core.networkutil)

    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.material3)
    implementation(libs.flowredux)
    implementation(libs.snapper)
}
