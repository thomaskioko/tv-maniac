@file:Suppress("UnstableApiUsage")

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(projects.shared.domain.show.api)
    implementation(projects.shared.domain.showCommon.api)
    implementation(projects.shared.interactors)
    implementation(libs.multiplatform.paging.core)
}
