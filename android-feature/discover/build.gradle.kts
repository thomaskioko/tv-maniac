plugins {
    alias(libs.plugins.tvmaniac.android)
}

tvmaniac {
    android {
        useCompose()
        useRoborazzi()
    }

    optIn(
        "androidx.compose.foundation.ExperimentalFoundationApi",
        "androidx.compose.material.ExperimentalMaterialApi",
        "androidx.compose.material3.ExperimentalMaterial3Api",
        "dev.chrisbanes.snapper.ExperimentalSnapperApi",
    )
}

dependencies {
    api(projects.presenter.discover)

    implementation(projects.androidDesignsystem)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.snapper)

    testImplementation(projects.core.screenshotTests)
}
