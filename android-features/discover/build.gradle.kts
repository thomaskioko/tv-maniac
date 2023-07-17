plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.details"
}

dependencies {
    api(projects.presentation.discover)

    implementation(projects.data.category.api)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.flowredux)
    implementation(libs.snapper)
}
