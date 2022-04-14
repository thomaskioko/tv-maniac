import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    api(projects.shared.core.ui)
    api(projects.shared.domain.persistence)
    implementation(projects.android.common.compose)
    implementation(projects.android.common.annotations)

    implementation(libs.accompanist.insetsui)

    testImplementation(libs.testing.junit)
    testImplementation(libs.testing.mockk.core)
}
