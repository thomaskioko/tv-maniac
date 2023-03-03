plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(project(":shared:data:trakt:api"))
    implementation(project(":shared:domain:following"))

    implementation(libs.accompanist.insetsui)
    implementation(libs.flowredux)
}
