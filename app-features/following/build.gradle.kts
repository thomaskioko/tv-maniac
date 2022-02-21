plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(projects.appCommon.compose)
    implementation(projects.shared.interactors)
    implementation(projects.shared.domain.show.api)
    implementation(projects.shared.domain.showCommon.api)
}
