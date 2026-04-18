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
    api(projects.features.trailers.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

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
