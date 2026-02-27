plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
    }
    useKotlinInject()
}

dependencies {
    api(projects.core.imageloading.api)
    api(projects.data.datastore.api)
    api(projects.core.base)

    api(libs.coil.base)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.coil.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coroutines.core)
    api(libs.okhttp.okhttp)
}
