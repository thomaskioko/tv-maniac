plugins {
    id("tvmaniac.android.library")
    id("tvmaniac.hilt")
}

android {
    namespace = "com.thomaskioko.tvmaniac.workmanager"
}

dependencies {

    implementation(project(":shared:core:util"))
    implementation(project(":shared:domain:trakt:api"))
    implementation(project(":shared:domain:shows:api"))

    implementation(libs.androidx.work.runtime)

    implementation(libs.hilt.work)
    implementation(libs.kermit)
}