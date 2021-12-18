@file:Suppress("UnstableApiUsage")

import util.libs

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {

    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {

    api(libs.androidx.appCompat)
    api(libs.androidx.palette)

    api(libs.androidx.compose.runtime.core)
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.ui.runtime)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.compiler)
    api(libs.androidx.compose.constraintlayout)
    api(libs.androidx.compose.activity)
    api(libs.androidx.compose.navigation)
    api(libs.androidx.compose.paging)

    api(libs.material)
    api(libs.kenburns)
    api(libs.coil)
    api(libs.snapper)

    api(libs.accompanist.insets)
    api(libs.accompanist.insetsui)
    api(libs.accompanist.pager.core)
    api(libs.accompanist.pager.indicator)
    api(libs.accompanist.systemuicontroller)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    kapt(libs.androidx.lifecycle.compiler)
}
