plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.videoplayer"
}

dependencies {
    api(project(":shared:domain:trailers:api"))

    implementation(libs.youtubePlayer)
    implementation(libs.accompanist.insetsui)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.androidx.compose.constraintlayout)
}