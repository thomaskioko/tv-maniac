plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasondetails"
}

dependencies {
    implementation(projects.presentation.seasondetails)
    implementation(projects.common.navigation)
    implementation(projects.common.voyagerutil)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.kotlinx.collections)
    implementation(libs.snapper)
}
