plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    api(projects.common.voyagerutil)

    implementation(projects.common.navigation)
    implementation(projects.core.traktAuth.api)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.flowredux)
    implementation(libs.kotlinx.collections)
    implementation(libs.snapper)
}
