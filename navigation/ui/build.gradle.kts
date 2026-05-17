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

    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.ui)
    implementation(platform(libs.androidx.compose.bom))
}
