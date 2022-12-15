import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    implementation(project(":android:core:compose"))
    implementation(project(":android:core:trakt-auth"))
    implementation(project(":shared:core:util"))
    implementation(project(":shared:core:database"))
    implementation(project(":shared:domain:trakt:api"))

    implementation(libs.flowredux)
    implementation(libs.snapper)
    implementation(libs.accompanist.insetsui)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
