plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.details"
}

dependencies {
    api(project(":shared:domain:discover"))
    implementation(project(":shared:data:category:api"))

    implementation(libs.androidx.compose.material)
    implementation(libs.accompanist.pager.core)

    implementation(libs.accompanist.pager.indicator)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.flowredux)
    implementation(libs.snapper)
}
