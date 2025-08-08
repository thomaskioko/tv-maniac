plugins {
    alias(libs.plugins.tvmaniac.android)
}

tvmaniac {
    android {
        useCompose()
    }
    useKotlinInject()
}

dependencies {
    api(projects.core.imageloading.api)
    api(projects.data.datastore.api)
    implementation(projects.core.base)
    implementation(projects.core.networkUtil)
    implementation(projects.core.util)

    implementation(libs.coil.compose)
    implementation(libs.coil.base)
    implementation(libs.coil.gif)
    implementation(libs.coroutines.core)
    api(libs.okhttp.okhttp)
}
