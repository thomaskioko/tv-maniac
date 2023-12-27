plugins {
    id("plugin.tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.feature.moreshows"
}

dependencies {
    api(projects.presentation.moreShows)

    implementation(projects.android.designsystem)
    implementation(projects.android.resources)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
}
