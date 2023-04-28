plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {
    implementation(projects.shared.data.category.api)
    implementation(projects.shared.data.shows.api)

    implementation(libs.androidx.compose.paging)
    implementation(libs.flowredux)
    implementation(libs.accompanist.insetsui)
}
