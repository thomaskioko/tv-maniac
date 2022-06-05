import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    api(project(":shared:core:ui"))
    api(project(":shared:core:persistence"))
    implementation(project(":android:common:compose"))

    implementation(libs.accompanist.insetsui)

    testImplementation(libs.testing.junit)
    testImplementation(libs.testing.mockk.core)
}
