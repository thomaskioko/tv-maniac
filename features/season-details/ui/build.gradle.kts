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
    api(projects.features.seasonDetails.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.core.view)
    implementation(projects.i18n.generator)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
