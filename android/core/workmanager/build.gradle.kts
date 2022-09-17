@file:Suppress("UnstableApiUsage")

import util.libs


plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.thomaskioko.tvmaniac.workmanager"
    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        manifestPlaceholders["appAuthRedirectScheme"] = "empty"
    }
}

dependencies {

    api(project(":shared:core:util"))
    api(libs.androidx.workmanager)

    implementation(project(":shared:domain:discover:api"))
    implementation(project(":shared:domain:trakt:api"))
    implementation(project(":shared:domain:show-common:api"))
    implementation(libs.kermit)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.hilt.work)
    kapt(libs.hilt.androidx.compiler)
}