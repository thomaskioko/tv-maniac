plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
    }

    optIn(
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.navigation.ui)
    api(projects.features.root.presenter)

    implementation(projects.navigation.api)
    implementation(projects.features.root.nav)
    implementation(projects.androidDesignsystem)

    api(libs.androidx.compose.runtime)
    implementation(libs.decompose.extensions.compose)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
}
