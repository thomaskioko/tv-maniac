@file:Suppress("UnstableApiUsage")

import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.showdetails"
}

dependencies {
    api(project(":shared:core:ui"))
    api(project(":shared:core:util"))
    api(projects.shared.domain.showDetails.api)
    api(project(":shared:domain:similar:api"))
    api(project(":shared:domain:genre:api"))
    api(projects.shared.domain.seasons.api)
    api(project(":shared:domain:show-common:api"))
    api(project(":shared:domain:last-air-episodes:api"))
    api(project(":shared:domain:trailers:api"))
    implementation(project(":android:common:compose"))

    implementation(libs.coil)
    implementation(libs.snapper)
    implementation(libs.youtubePlayer)
    implementation(libs.accompanist.insetsui)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
