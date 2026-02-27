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
    )
}

dependencies {
    api(projects.presenter.profile)

    implementation(projects.androidDesignsystem)
    implementation(projects.data.datastore.api)
    implementation(projects.core.view)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.coil.compose)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
