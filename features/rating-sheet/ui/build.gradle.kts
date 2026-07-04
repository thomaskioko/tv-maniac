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
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.core.base)
    api(projects.features.ratingSheet.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    api(libs.androidx.compose.runtime)
    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
