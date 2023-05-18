plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    implementation(projects.androidCore.traktAuth)
    implementation(projects.data.profile.api)
    implementation(projects.data.profilestats.api)

    implementation(libs.flowredux)
    implementation(libs.snapper)
    implementation(libs.accompanist.insetsui)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
