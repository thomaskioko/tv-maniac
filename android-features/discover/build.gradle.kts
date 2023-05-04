plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.details"
}

dependencies {
    api(projects.domain.discover)
    implementation(projects.data.category.api)

    implementation(libs.accompanist.pager.core)

    implementation(libs.accompanist.pager.indicator)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.flowredux)
    implementation(libs.snapper)
}
