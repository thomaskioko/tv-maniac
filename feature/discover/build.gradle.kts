plugins {
    id("plugin.tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.feature.discover"
}

dependencies {
    api(projects.presentation.discover)

    implementation(projects.androidCore.designsystem)
    implementation(projects.androidCore.resources)
    implementation(projects.data.category.api) //TODO:: Remove this and just pass the title

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.snapper)
}
