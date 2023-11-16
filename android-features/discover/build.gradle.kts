plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.details"
}

dependencies {
    api(projects.presentation.discover)

    implementation(projects.common.localization)
    implementation(projects.data.category.api)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.flowredux)
    implementation(libs.kotlinx.collections)
    implementation(libs.snapper)
}
