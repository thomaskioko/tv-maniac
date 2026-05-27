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
    api(projects.features.myShows.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    api(libs.androidx.compose.runtime)
    implementation(projects.features.continueWatching.presenter)
    implementation(projects.features.continueWatching.ui)
    implementation(projects.features.startWatching.presenter)
    implementation(projects.features.startWatching.ui)
    implementation(projects.features.home.nav)
    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.data.watchlistPrefs.api)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.collections)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
