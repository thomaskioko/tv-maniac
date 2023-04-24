plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.search"
}

dependencies {

    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)

    implementation(projects.android.core.resources)

    implementation(libs.androidx.compose.material3)
    implementation(libs.coroutines.core)
}
