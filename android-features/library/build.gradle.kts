plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.library"
}

dependencies {
    api(projects.common.voyagerutil)

    implementation(projects.common.navigation)
    implementation(projects.data.shows.api)

    implementation(libs.flowredux)
    implementation(libs.kotlinx.collections)
}
