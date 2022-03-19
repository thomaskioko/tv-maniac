plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(projects.appCommon.compose)
    implementation(projects.shared.domain.showDetails.api)
    implementation(projects.shared.domain.showCommon.api)
}
