plugins {
    id("plugin.tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    api(projects.presentation.settings)

    //TODO:: Get rid of core and data dependencies.
    implementation(projects.core.datastore.api)
    implementation(projects.data.shows.api)

    implementation(projects.android.designsystem)
    implementation(projects.android.resources)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
    implementation(libs.kotlinx.collections)

}
