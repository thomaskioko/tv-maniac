plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.details"
}

dependencies {
    api(project(":shared:domain:shows:api"))

    api(libs.androidx.compose.material)
    api(libs.accompanist.pager.core)

    implementation(libs.snapper)

    implementation(libs.accompanist.pager.indicator)
    implementation(libs.androidx.compose.ui.util)
}
