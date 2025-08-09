plugins {
    alias(libs.plugins.tvmaniac.android)
}

tvmaniac {
    android {
        useCompose()
        useRoborazzi()
    }

    optIn(
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.presenter.settings)
    api(projects.data.datastore.api)

    implementation(projects.androidDesignsystem)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.coil.compose)

    testImplementation(projects.core.screenshotTests)
}
