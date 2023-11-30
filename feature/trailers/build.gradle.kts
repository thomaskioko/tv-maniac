plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.videoplayer"
}

dependencies {
    implementation(projects.presentation.trailers)
    implementation(projects.common.navigation)
    implementation(projects.common.voyagerutil)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.kotlinx.collections)
    implementation(libs.youtubePlayer)
}