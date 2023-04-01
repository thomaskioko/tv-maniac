plugins {
    id("tvmaniac.android.library")
    id("tvmaniac.hilt")
}

android {
    namespace = "com.thomaskioko.tvmaniac.workmanager"
}

dependencies {

    implementation(project(":shared:core:util"))
    implementation(project(":shared:data:profile:api"))
    implementation(project(":shared:data:shows:api"))

    implementation(libs.androidx.work.runtime)

    implementation(libs.hilt.work)
    implementation(libs.kermit)
}