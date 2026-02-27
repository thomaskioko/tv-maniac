plugins { alias(libs.plugins.app.android) }

scaffold {
    android {
        useCompose()
    }
    optIn(
        "com.github.takahirom.roborazzi.ExperimentalRoborazziApi",
    )
}

dependencies {
    api(libs.androidx.compose.runtime)

    implementation(projects.androidDesignsystem)
    implementation(projects.data.datastore.api)

    implementation(libs.androidx.compose.ui.test)
    implementation(libs.differ)
    implementation(libs.roborazzi)
    implementation(libs.roborazzi.core)
    implementation(libs.shadows.framework)

    runtimeOnly(libs.robolectric)
    runtimeOnly(libs.androidx.compose.ui.test.manifest)
}
