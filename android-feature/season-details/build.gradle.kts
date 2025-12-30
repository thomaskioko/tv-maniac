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
        "androidx.compose.material3.ExperimentalMaterial3Api",
        "dev.chrisbanes.snapper.ExperimentalSnapperApi",
    )
}

dependencies {
    api(projects.presenter.seasondetails)

    implementation(projects.androidDesignsystem)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.snapper)

    testImplementation(projects.core.screenshotTests)
}
