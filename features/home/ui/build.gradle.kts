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
    api(projects.features.home.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    api(libs.androidx.compose.runtime)
    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.i18n.generator)
    implementation(projects.features.discover.nav)
    implementation(projects.features.library.nav)
    implementation(projects.features.profile.nav)
    implementation(projects.features.progress.nav)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.decompose.extensions.compose)
}
