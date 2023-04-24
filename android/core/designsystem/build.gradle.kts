plugins {
    id("tvmaniac.compose.library")
}

android {
    namespace = "com.thomaskioko.tvmaniac.compose"
}

dependencies {
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.palette)
    api(libs.coil.coil)
    api(libs.coil.compose)

    implementation(projects.android.core.resources)

    implementation(libs.accompanist.insetsui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.collection)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.core)
    implementation(libs.coroutines.jvm)
    implementation(libs.kenburns)
}
