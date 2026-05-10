plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    useCodegen()

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
    api(projects.core.base)
    api(projects.features.progress.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    implementation(projects.androidDesignsystem)
    implementation(projects.features.home.nav)
    implementation(projects.core.testTags)
    implementation(projects.features.calendar.ui)
    implementation(projects.features.upnext.ui)
    implementation(projects.features.calendar.presenter)
    implementation(projects.features.upnext.presenter)
    implementation(projects.i18n.generator)
    implementation(projects.core.view)
    implementation(projects.domain.theme)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
