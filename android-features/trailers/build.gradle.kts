plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.videoplayer"
}

dependencies {
    api(projects.presentation.trailers)
    implementation(projects.common.navigation)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.flowredux)
    implementation(libs.youtubePlayer)
}