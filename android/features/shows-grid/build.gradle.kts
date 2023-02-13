plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {
    implementation(project(":shared:domain:shows:api"))
    implementation(project(":shared:domain:trakt:api"))
    implementation(project(":shared:domain:show-details:api"))

    implementation(libs.androidx.compose.paging)
    implementation(libs.flowredux)
    implementation(libs.accompanist.insetsui)
}
