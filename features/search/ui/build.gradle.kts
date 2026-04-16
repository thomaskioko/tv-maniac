plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
        useRoborazzi()
    }

    optIn(
        "androidx.compose.material3.ExperimentalMaterial3Api",
        "dev.chrisbanes.snapper.ExperimentalSnapperApi",
    )
}

dependencies {
    api(projects.features.search.presenter)

    implementation(projects.androidDesignsystem)
    implementation(projects.data.datastore.api)
    implementation(projects.data.genre.api)
    implementation(projects.i18n.generator)
    implementation(projects.core.view)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.snapper)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
