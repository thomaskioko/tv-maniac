plugins {
    id("tvmaniac.android.library")
    id("tvmaniac.hilt")
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth"
}

dependencies {

    implementation(project(":shared:core:util"))
    implementation(project(":android:core:workmanager"))

    implementation(libs.appauth)
    implementation(libs.androidx.core)
    implementation(libs.androidx.work.runtime)
    implementation(libs.kermit)
}