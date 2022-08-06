plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.videoplayer"
}

dependencies {

    implementation(projects.android.common.compose)
    implementation(libs.accompanist.insetsui)
    implementation(libs.accompanist.navigation.material)
}