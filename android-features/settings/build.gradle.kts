plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    implementation(projects.androidCore.traktAuth)
    implementation(projects.core.datastore.api)
    implementation(projects.data.shows.api)
    implementation(projects.domain.settings)

    implementation(libs.accompanist.insetsui)
    implementation(libs.flowredux)

}
