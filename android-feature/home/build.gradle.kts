plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
    }
}

dependencies {
    api(projects.presenter.home)

    implementation(projects.androidDesignsystem)
    implementation(projects.i18n.generator)

    implementation(projects.androidFeature.discover)
    implementation(projects.androidFeature.watchlist)
    implementation(projects.androidFeature.search)
    implementation(projects.androidFeature.settings)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
}
