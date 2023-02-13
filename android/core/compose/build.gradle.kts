plugins {
    id("tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.compose"
}

dependencies {
    implementation(project(":android:core:resources"))

    api(libs.coroutines.jvm)
    api(libs.coil.coil)
    api(libs.coil.compose)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.palette)
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.ui.runtime)
    implementation(libs.kenburns)
    implementation(libs.accompanist.insetsui)
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons)
}
