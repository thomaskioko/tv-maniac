plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    implementation(projects.androidCore.traktAuth)
    implementation(projects.shared.core.datastore.api)
    implementation(projects.shared.data.shows.api)
    implementation(projects.shared.domain.settings)

    implementation(libs.accompanist.insetsui)
    implementation(libs.flowredux)

}
