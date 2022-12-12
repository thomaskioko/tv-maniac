import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(project(":shared:domain:following:api"))
    implementation(project(":shared:domain:trakt:api"))
    implementation(projects.android.core.compose)

    implementation(libs.accompanist.insetsui)
}
