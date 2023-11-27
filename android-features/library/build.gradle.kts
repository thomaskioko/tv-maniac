plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.library"
}

dependencies {
    implementation(projects.data.shows.api)
    implementation(projects.presentation.library)
    implementation(projects.common.navigation)

    implementation(libs.flowredux)
}
