plugins {
    id("tvmaniac.android.library")
    id("tvmaniac.hilt")
}

android {
    namespace = "com.thomaskioko.tvmaniac.workmanager"
}

dependencies {

    implementation(project(":shared:core:util"))
    implementation(project(":shared:data:trakt:api"))
    implementation(project(":shared:data:trakt-profile:api"))

    implementation(libs.androidx.work.runtime)

    implementation(libs.hilt.work)
    implementation(libs.kermit)
}