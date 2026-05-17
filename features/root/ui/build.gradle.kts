plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    useCodegen()

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
    implementation(projects.features.home.ui)
    implementation(projects.features.home.presenter)
    implementation(projects.androidDesignsystem)
    implementation(projects.core.base)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.material3)
}
