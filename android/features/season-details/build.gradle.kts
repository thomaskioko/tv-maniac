plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasondetails"
}

dependencies {
    implementation(project(":shared:domain:season-details:api"))

    implementation(libs.snapper)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
