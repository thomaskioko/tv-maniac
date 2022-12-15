import util.libs

plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth"
    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        manifestPlaceholders["appAuthRedirectScheme"] = "empty"
    }
}

dependencies {

    implementation(project(":android:core:workmanager"))
    implementation(project(":shared:core:util"))
    implementation(libs.inject)
    implementation(libs.appauth)
    implementation(libs.androidx.core)
    implementation(libs.hilt.android)
    implementation(libs.kermit)
    kapt(libs.hilt.compiler)
}