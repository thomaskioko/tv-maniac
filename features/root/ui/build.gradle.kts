plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
    }

    optIn(
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.navigation.api)
    api(projects.features.root.presenter)
    implementation(projects.features.root.nav)
    implementation(projects.androidDesignsystem)

    implementation(projects.features.debug.presenter)
    implementation(projects.features.debug.ui)
    implementation(projects.features.episodeDetail.presenter)
    implementation(projects.features.episodeDetail.ui)
    implementation(projects.features.home.presenter)
    implementation(projects.features.home.ui)
    implementation(projects.features.moreShows.presenter)
    implementation(projects.features.moreShows.ui)
    implementation(projects.features.search.presenter)
    implementation(projects.features.search.ui)
    implementation(projects.features.seasonDetails.presenter)
    implementation(projects.features.seasonDetails.ui)
    implementation(projects.features.settings.presenter)
    implementation(projects.features.settings.ui)
    implementation(projects.features.showDetails.presenter)
    implementation(projects.features.showDetails.ui)
    implementation(projects.features.trailers.presenter)
    implementation(projects.features.trailers.ui)

    api(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
}
