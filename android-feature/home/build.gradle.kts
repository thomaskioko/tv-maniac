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
    implementation(projects.androidFeature.library)
    implementation(projects.androidFeature.profile)
    implementation(projects.androidFeature.upnext)
    implementation(projects.presenter.discover)
    implementation(projects.presenter.library)
    implementation(projects.presenter.profile)
    implementation(projects.presenter.upnext)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
}
