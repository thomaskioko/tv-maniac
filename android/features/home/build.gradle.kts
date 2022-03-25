plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.home"
}

dependencies {
    implementation(projects.android.common.compose)
}
