plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile"
}

dependencies {
    implementation(project(":android:core:trakt-auth"))
    implementation(project(":shared:data:trakt-profile:api"))

    implementation(libs.flowredux)
    implementation(libs.snapper)
    implementation(libs.accompanist.insetsui)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
