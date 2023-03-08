plugins {
    id("tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.navigation"
}

dependencies {

    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigationcompose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.accompanist.navigation.material)
    runtimeOnly(libs.coroutines.android)
}
