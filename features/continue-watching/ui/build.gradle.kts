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
        "androidx.compose.material.ExperimentalMaterialApi",
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.features.continueWatching.presenter)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.core.view)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.collections)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
