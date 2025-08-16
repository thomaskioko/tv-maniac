plugins {
    alias(libs.plugins.tvmaniac.android)
}

tvmaniac {
    android {
        useCompose()
    }
    useDependencyInjection()
}

dependencies {
    api(projects.core.imageloading.api)
    api(projects.data.datastore.api)
    implementation(projects.core.base)

    implementation(libs.coil.compose)
    implementation(libs.coil.base)
    implementation(libs.coil.gif)
    implementation(libs.coroutines.core)
    api(libs.okhttp.okhttp)
}
