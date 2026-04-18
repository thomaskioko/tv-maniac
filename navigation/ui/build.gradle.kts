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
    api(projects.navigation.api)

    implementation(projects.core.base)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.ui)
}
