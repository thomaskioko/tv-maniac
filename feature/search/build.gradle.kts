plugins {
    id("plugin.tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.search"
}

dependencies {
    api(projects.presentation.search)

    implementation(projects.androidCore.designsystem)
    implementation(projects.androidCore.resources)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
}
