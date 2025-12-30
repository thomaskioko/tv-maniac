plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    explicitApi()

    android {
        useCompose()
    }
}

dependencies {
    api(projects.presenter.trailers)

    implementation(projects.androidDesignsystem)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.coil.compose)
    implementation(libs.youtubePlayer)
}
