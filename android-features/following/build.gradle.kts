plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(projects.data.shows.api)
    implementation(projects.presentation.following)

    implementation(libs.accompanist.insetsui)
    implementation(libs.flowredux)
}
