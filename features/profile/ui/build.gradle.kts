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
    api(projects.features.profile.presenter)

    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.domain.theme)
    implementation(projects.core.view)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.coil.compose)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
