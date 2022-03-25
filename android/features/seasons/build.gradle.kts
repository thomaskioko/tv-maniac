plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasons"
}

dependencies {
    implementation(projects.shared.domain.showDetails.api)
    implementation(projects.shared.domain.seasons.api)
    implementation(projects.shared.domain.showCommon.api)
    implementation(projects.shared.domain.seasonEpisodes.api)
}
