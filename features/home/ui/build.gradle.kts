plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
    }
}

dependencies {
    api(projects.features.home.presenter)

    implementation(projects.androidDesignsystem)
    implementation(projects.i18n.generator)

    implementation(projects.features.discover.ui)
    implementation(projects.features.library.ui)
    implementation(projects.features.profile.ui)
    implementation(projects.features.progress.ui)
    implementation(projects.features.discover.presenter)
    implementation(projects.features.library.presenter)
    implementation(projects.features.profile.presenter)
    implementation(projects.features.progress.presenter)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
}
