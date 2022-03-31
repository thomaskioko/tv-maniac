import util.libs

plugins {
    `android-app-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac"
}

dependencies {
    implementation(projects.shared.core.util)
    implementation(projects.shared.database)
    implementation(projects.shared.remote)
    implementation(projects.shared.domain.showDetails.api)
    implementation(projects.shared.domain.showCommon.api)
    implementation(projects.shared.domain.showDetails.implementation)
    implementation(projects.shared.domain.seasons.api)
    implementation(projects.shared.domain.seasons.implementation)
    implementation(projects.shared.domain.episodes.api)
    implementation(projects.shared.domain.episodes.implementation)
    implementation(projects.shared.domain.genre.api)
    implementation(projects.shared.domain.genre.implementation)
    implementation(projects.shared.domain.lastAirEpisodes.api)
    implementation(projects.shared.domain.lastAirEpisodes.implementation)
    implementation(projects.shared.domain.similar.api)
    implementation(projects.shared.domain.similar.implementation)
    implementation(projects.shared.domain.seasonEpisodes.api)
    implementation(projects.shared.domain.seasonEpisodes.implementation)
    implementation(projects.shared.domain.discover.api)
    implementation(projects.shared.domain.discover.implementation)
    implementation(projects.shared.domain.persistence)
    implementation(projects.android.common.annotations)
    implementation(projects.android.common.compose)
    implementation(projects.android.common.navigation)
    implementation(projects.android.features.discover)
    implementation(projects.android.features.home)
    implementation(projects.android.features.search)
    implementation(projects.android.features.showDetails)
    implementation(projects.android.features.showsGrid)
    implementation(projects.android.features.following)
    implementation(projects.android.features.settings)
    implementation(projects.android.features.seasons)

    implementation(libs.accompanist.insets)
    implementation(libs.androidx.compose.activity)
    implementation(libs.accompanist.systemuicontroller)
}
