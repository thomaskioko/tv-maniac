plugins {
    id("plugin.tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    api(projects.presentation.profile)

    implementation(projects.androidCore.designsystem)
    implementation(projects.androidCore.resources)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
    implementation(libs.snapper)
}
