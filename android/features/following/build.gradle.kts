plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    implementation(project(":shared:domain:following:api"))
    implementation(project(":shared:domain:trakt:api"))

    implementation(libs.accompanist.insetsui)
}
