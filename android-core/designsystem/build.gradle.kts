plugins {
    id("tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.compose"
}

dependencies {
    api(libs.androidx.compose.material.icons)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.palette)
    api(libs.coil.coil)
    api(libs.coil.compose)
    api(libs.coroutines.jvm)

    implementation(projects.common.localization)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core)
    implementation(libs.kenburns)
}
