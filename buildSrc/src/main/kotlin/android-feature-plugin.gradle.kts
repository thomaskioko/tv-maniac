@file:Suppress("UnstableApiUsage")

import util.libs

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        val androidTest by getting
        val test by getting
        androidTest.java.srcDirs("src/androidTest/kotlin")
        test.java.srcDirs("src/test/kotlin")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.get()
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":app-common:core"))
    implementation(project(":app-common:compose"))
    implementation(project(":app-common:navigation"))

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    kapt(libs.hilt.compiler)

    testImplementation(libs.testing.turbine)
    testImplementation(libs.testing.coroutines.test)
    testImplementation(libs.testing.kotest.assertions)

    testImplementation(libs.testing.mockito.inline)
    testImplementation(libs.testing.mockk.core)

    testImplementation(libs.testing.androidx.core)

    testRuntimeOnly(libs.testing.junit5.jupiter)
    testRuntimeOnly(libs.testing.junit5.engine)
    testRuntimeOnly(libs.testing.junit5.vintage)
}
