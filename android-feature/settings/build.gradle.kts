plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        enableAndroidResources()

        useCompose()
        useRoborazzi()
    }

    optIn(
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.presenter.settings)
    implementation(projects.data.datastore.api)

    implementation(projects.androidDesignsystem)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.foundation)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.coil.compose)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
