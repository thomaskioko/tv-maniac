plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    api(projects.common.voyagerutil)

    implementation(projects.core.datastore.api)
    implementation(projects.core.traktAuth.api)
    implementation(projects.data.shows.api)
    implementation(projects.presentation.settings)
    implementation(projects.common.navigation)

    implementation(libs.flowredux)
    implementation(libs.kotlinx.collections)

}
