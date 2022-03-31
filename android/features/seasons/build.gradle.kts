import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasons"
}

dependencies {
    api(projects.shared.core.ui)
    api(projects.shared.core.util)
    api(projects.shared.domain.showDetails.api)
    api(projects.shared.domain.seasons.api)
    api(projects.shared.domain.seasonEpisodes.api)
    implementation(projects.shared.domain.showCommon.api)
    implementation(projects.android.common.compose)

    implementation(libs.snapper)
    implementation(libs.accompanist.insets)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
