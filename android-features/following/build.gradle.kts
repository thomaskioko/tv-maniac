plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(projects.shared.data.shows.api)
    implementation(projects.shared.domain.following)

    implementation(libs.accompanist.insetsui)
    implementation(libs.flowredux)
}
