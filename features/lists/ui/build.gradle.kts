plugins {
    alias(libs.plugins.app.android)
}

android {
    namespace = "com.thomaskioko.tvmaniac.android.feature.lists"
}

scaffold {
    useCodegen()

    android {
        enableAndroidResources()

        useCompose()
        useRoborazzi()
    }

    optIn(
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.core.base)
    api(projects.features.lists.presenter)
    api(projects.navigation.api)
    api(projects.navigation.ui)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.robolectric.annotations)
    testImplementation(projects.core.screenshotTests)

    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.paging.common)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.collections)
    implementation(libs.moko.resources)
    testImplementation(libs.androidx.activity.base)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.junit4)
}
