plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    android {
        useCompose()
    }

    optIn(
        "androidx.compose.foundation.ExperimentalFoundationApi",
        "androidx.compose.material.ExperimentalMaterialApi",
        "androidx.compose.material3.ExperimentalMaterial3Api",
    )
}

dependencies {
    api(projects.features.library.presenter)

    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.core.view)
    implementation(projects.domain.theme)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.collections)
}
