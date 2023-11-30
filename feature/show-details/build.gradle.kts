plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.showdetails"
}

dependencies {
    implementation(projects.common.voyagerutil)
    implementation(projects.presentation.showDetails)
    implementation(projects.common.navigation)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.kotlinx.collections)
    implementation(libs.snapper)
}
