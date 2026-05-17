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
        "dev.chrisbanes.snapper.ExperimentalSnapperApi",
    )
}

dependencies {
    api(projects.core.base)
    api(projects.features.showDetails.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    implementation(projects.features.showDetails.nav)
    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.core.view)
    implementation(projects.i18n.generator)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.material3)
    implementation(libs.coil.compose)
    implementation(libs.snapper)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
