plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
        useRoborazzi()
    }

    optIn(
        "androidx.compose.foundation.ExperimentalFoundationApi",
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.features.progress.presenter)

    implementation(projects.androidDesignsystem)
    implementation(projects.features.calendar.ui)
    implementation(projects.features.upnext.ui)
    implementation(projects.features.calendar.presenter)
    implementation(projects.features.upnext.presenter)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
