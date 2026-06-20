plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    useCodegen()

    android {
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
    )
}

dependencies {
    api(projects.core.base)
    api(projects.features.discover.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(projects.androidDesignsystem)
    implementation(projects.features.home.nav)
    implementation(projects.core.testTags)
    implementation(projects.core.view)
    implementation(projects.i18n.generator)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)
}
