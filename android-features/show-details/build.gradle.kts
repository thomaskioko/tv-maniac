plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.showdetails"
}

dependencies {
    api(projects.presentation.showDetails)
    implementation(projects.common.navigation)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.flowredux)
    implementation(libs.snapper)
}
