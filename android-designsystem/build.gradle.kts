plugins { alias(libs.plugins.app.android) }

scaffold {
    android {
        enableAndroidResources()

        useCompose()
        useRoborazzi()
    }

    optIn("androidx.compose.material3.ExperimentalMaterial3Api")
}

dependencies {
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.compose.material.icons)
    api(libs.androidx.compose.runtime)

    api(projects.domain.theme)
    implementation(projects.core.testTags)
    implementation(projects.i18n.generator)

    api(libs.coil.base)
    api(libs.moko.resources.compose)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.collections)
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.compose.foundation)
    api(libs.kotlinx.collections)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.coil.coil)
    implementation(libs.coil.compose)
    implementation(libs.kenburns)
    implementation(libs.androidx.palette)
    implementation(libs.coroutines.jvm)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
