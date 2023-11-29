plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {
    implementation(projects.data.category.api)
    implementation(projects.data.shows.api)
    implementation(projects.common.navigation)

    implementation(libs.androidx.compose.paging)
}
