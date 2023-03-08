plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {
    implementation(project(":shared:data:trakt:api"))
    implementation(project(":shared:data:category:api"))

    implementation(libs.androidx.compose.paging)
    implementation(libs.flowredux)
    implementation(libs.accompanist.insetsui)
}
