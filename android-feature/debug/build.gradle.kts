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
    api(projects.presenter.debug)

    implementation(projects.androidDesignsystem)
    implementation(projects.core.view)
    implementation(projects.data.datastore.api)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
}
