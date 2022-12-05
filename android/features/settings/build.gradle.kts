import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    api(project(":shared:core:util"))
    api(project(":shared:domain:settings"))
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:domain:trakt:api"))
    implementation(project(":android:core:compose"))
    implementation(project( ":android:core:trakt-auth"))

    implementation(libs.accompanist.insetsui)

    testImplementation(libs.testing.junit)
    testImplementation(libs.testing.mockk.core)
}
