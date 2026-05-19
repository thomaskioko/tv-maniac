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
    api(projects.features.watchlist.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.core.view)
    implementation(projects.features.home.nav)
    implementation(projects.i18n.generator)
    implementation(projects.data.watchlist.api)

    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.collections)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
