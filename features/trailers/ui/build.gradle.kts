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
    implementation(projects.i18n.generator)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.coil.compose)
    implementation(libs.youtubePlayer)
}
