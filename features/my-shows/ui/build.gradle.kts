plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    useCodegen()

    android {
        useCompose()
    }
}

dependencies {
    api(projects.core.base)
    api(projects.features.myShows.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    api(libs.androidx.compose.runtime)
    implementation(projects.features.continueWatching.presenter)
    implementation(projects.features.continueWatching.ui)
    implementation(projects.features.home.nav)
}
