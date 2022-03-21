plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    implementation(projects.shared.domain.persistence)
}
