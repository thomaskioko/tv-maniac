import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.details"
}

dependencies {
    api(project(":shared:core:ui"))
    api(project(":shared:core:util"))
    api(projects.android.core.compose)

    api(libs.inject)
    api(libs.androidx.compose.material)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.accompanist.pager.core)

    api(projects.shared.domain.discover.api)
    api(project(":shared:domain:show-common:api"))

    implementation(libs.snapper)

    implementation(libs.accompanist.pager.indicator)
    implementation(libs.androidx.compose.ui.util)
}
