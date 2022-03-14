plugins {
    `android-app-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac"
}

dependencies {
    implementation(projects.shared.core)
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
    implementation(projects.shared.interactors)
    implementation(projects.appCommon.annotations)
    implementation(projects.appCommon.compose)
    implementation(projects.appCommon.resources)
    implementation(projects.appCommon.navigation)
    implementation(projects.appFeatures.discover)
    implementation(projects.appFeatures.home)
    implementation(projects.appFeatures.search)
    implementation(projects.appFeatures.showDetails)
    implementation(projects.appFeatures.showsGrid)
    implementation(projects.appFeatures.following)
    implementation(projects.appFeatures.settings)
    implementation(projects.appFeatures.seasons)
}
