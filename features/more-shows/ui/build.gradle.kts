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
    api(projects.core.base)
    api(projects.features.moreShows.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    implementation(projects.androidDesignsystem)
    implementation(projects.domain.theme)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
