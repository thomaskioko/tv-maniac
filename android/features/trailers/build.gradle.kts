plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.videoplayer"
}

dependencies {
    api(projects.shared.domain.trailers)

    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.coroutines.core)

    implementation(projects.android.core.resources)

    implementation(libs.accompanist.navigation.material)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.youtubePlayer)

}