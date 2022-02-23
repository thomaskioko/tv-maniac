plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(projects.shared.domain.show.api)
    implementation(projects.shared.domain.seasons.api)
    implementation(projects.shared.domain.showCommon.api)
    implementation(projects.shared.domain.seasonEpisodes.api)
}
