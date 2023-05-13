plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasondetails"
}

dependencies {
    implementation(projects.presentation.seasondetails)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.flowredux)
    implementation(libs.snapper)
}
