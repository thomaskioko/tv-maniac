plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
    }
}

dependencies {
    api(projects.presenter.trailers)

    implementation(projects.androidDesignsystem)
    implementation(projects.data.datastore.api)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.coil.compose)
    implementation(libs.youtubePlayer)
}
