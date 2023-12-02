plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    api(projects.common.voyagerutil)

    implementation(projects.common.navigation)
    implementation(projects.core.datastore.api)
    implementation(projects.core.traktAuth.api)
    implementation(projects.data.shows.api)
    implementation(projects.presentation.settings)
    implementation(projects.presentation.settings)

    implementation(libs.kotlinx.collections)

}