plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(projects.data.shows.api)
    implementation(projects.presentation.watchlist)

    implementation(projects.common.localization)

    implementation(libs.flowredux)
}
