plugins {
    alias(libs.plugins.app.android)
}

android {
    namespace = "com.thomaskioko.tvmaniac.android.feature.settings"
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
    api(projects.features.settings.presenter)
    implementation(projects.data.datastore.api)

    implementation(projects.androidDesignsystem)
    implementation(projects.core.view)
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
