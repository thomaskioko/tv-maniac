plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    implementation(projects.core.traktAuth.api)
    implementation(projects.presentation.profile)

    implementation(libs.flowredux)
    implementation(libs.snapper)
    implementation(libs.accompanist.insetsui)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
