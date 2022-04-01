import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.details"
}

dependencies {
    api(projects.shared.core.ui)
    api(projects.shared.core.util)
    api(projects.android.common.compose)

    api(libs.inject)
    api(libs.androidx.compose.material)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.accompanist.pager.core)

    api(projects.shared.domain.discover.api)
    api(projects.shared.domain.showCommon.api)

    implementation(libs.accompanist.insets)
    implementation(libs.snapper)

    implementation(libs.accompanist.pager.indicator)
    implementation(libs.androidx.compose.ui.util)
}
