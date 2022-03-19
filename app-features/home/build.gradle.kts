plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.home"
}

dependencies {
    implementation(projects.appCommon.compose)
}
