@file:Suppress("UnstableApiUsage")

import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.showdetails"
}

dependencies {
    api(projects.shared.core.ui)
    api(projects.shared.core.util)
    api(projects.shared.domain.showDetails.api)
    api(projects.shared.domain.similar.api)
    api(projects.shared.domain.genre.api)
    api(projects.shared.domain.seasons.api)
    api(projects.shared.domain.showCommon.api)
    api(projects.shared.domain.lastAirEpisodes.api)
    implementation(projects.android.common.compose)

    implementation(libs.snapper)
    implementation(libs.accompanist.insetsui)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
