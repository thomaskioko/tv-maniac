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
    implementation(projects.androidDesignsystem)
    implementation(projects.data.datastore.api)

    implementation(libs.androidx.compose.ui.test)
    implementation(libs.robolectric)
    implementation(libs.roborazzi)

    runtimeOnly(libs.androidx.compose.ui.test.manifest)
}
