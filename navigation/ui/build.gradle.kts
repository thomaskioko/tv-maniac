plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    useMetro()

    android {
        useCompose()
    }
}

dependencies {
    api(projects.core.base)
    api(projects.navigation.api)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.ui)
}
