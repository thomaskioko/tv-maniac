plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(projects.android.common.compose)
    implementation(projects.shared.domain.showDetails.api)
    implementation(projects.shared.domain.showCommon.api)
}
