import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.videoplayer"
}

dependencies {
    api(project(":shared:domain:trailers:api"))

    implementation(projects.android.core.compose)

    implementation(libs.youtubePlayer)
    implementation(libs.accompanist.insetsui)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.androidx.compose.constraintlayout)
}