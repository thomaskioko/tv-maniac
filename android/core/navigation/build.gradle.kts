@file:Suppress("UnstableApiUsage")

import util.libs

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    namespace = "com.thomaskioko.tvmaniac.navigation"

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {

    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)

    implementation(libs.androidx.compose.navigation)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.hilt.navigation)

    releaseImplementation(libs.androidx.compose.runtime)
}
