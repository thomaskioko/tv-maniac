plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        explicitApi()
        useCompose()
        useRoborazzi()

        libraryConfiguration {
            lint {
                baseline = file("lint-baseline.xml")
                disable += "UsingMaterialAndMaterial3Libraries"
            }
        }
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
