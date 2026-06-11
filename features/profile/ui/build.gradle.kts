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
    api(projects.features.profile.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.annotation)
    implementation(projects.data.accountManager.api)
    implementation(projects.androidDesignsystem)
    implementation(projects.features.home.nav)
    implementation(projects.core.testTags)
    implementation(projects.core.view)
    implementation(libs.androidx.compose.material3)
    implementation(libs.coil.compose)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
