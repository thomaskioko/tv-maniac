plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
        useRoborazzi()
    }

    optIn(
        "androidx.compose.foundation.ExperimentalFoundationApi",
        "androidx.compose.material.ExperimentalMaterialApi",
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.features.upnext.presenter)

    implementation(projects.androidDesignsystem)
    implementation(projects.core.view)
    implementation(projects.i18n.generator)
    implementation(projects.domain.upnext)

    implementation(projects.data.datastore.api)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.collections)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
