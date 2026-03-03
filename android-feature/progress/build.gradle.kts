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
    api(projects.presenter.progress)

    implementation(projects.androidDesignsystem)
    implementation(projects.androidFeature.calendar)
    implementation(projects.androidFeature.upnext)
    implementation(projects.presenter.calendar)
    implementation(projects.presenter.upnext)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
