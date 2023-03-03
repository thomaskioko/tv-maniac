plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.showdetails"
}

dependencies {
    api(project(":shared:domain:show-details"))

    implementation(libs.accompanist.insetsui)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.flowredux)
    implementation(libs.snapper)
}
