import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasons"
}

dependencies {
    api(project(":shared:core:ui"))
    api(project(":shared:core:util"))
    api(projects.shared.domain.showDetails.api)
    api(projects.shared.domain.seasons.api)
    api(projects.shared.domain.seasonEpisodes.api)
    implementation(project(":shared:domain:show-common:api"))
    implementation(projects.android.common.compose)

    implementation(libs.snapper)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
