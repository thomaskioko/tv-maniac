plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(project(":shared:data:shows:api"))
    implementation(project(":shared:domain:following"))

    implementation(libs.accompanist.insetsui)
    implementation(libs.flowredux)
}
