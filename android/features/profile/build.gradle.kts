import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    api(project(":shared:core:util"))

    implementation(project(":android:core:compose"))
    implementation(project(":android:core:trakt-auth"))
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:domain:trakt:api"))

    implementation(libs.snapper)
    implementation(libs.accompanist.insetsui)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
