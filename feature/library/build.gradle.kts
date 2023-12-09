plugins {
    id("plugin.tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.feature.library"
}

dependencies {
    api(projects.presentation.library)

    implementation(projects.androidCore.designsystem)
    implementation(projects.androidCore.resources)
    implementation(projects.data.shows.api) //TODO:: Remove this

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
    implementation(libs.kotlinx.collections)
}
