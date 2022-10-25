import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    implementation(projects.android.core.compose)
    implementation(libs.accompanist.insetsui)
}
