plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    implementation(projects.core.traktAuth.api)
    implementation(projects.presentation.profile)

    implementation(projects.common.localization)

    implementation(libs.flowredux)
    implementation(libs.snapper)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
