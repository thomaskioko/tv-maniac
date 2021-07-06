@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlin.gradle)
    implementation(libs.android.gradle)
    implementation(libs.plugins.dependency.check)
    implementation(libs.plugins.hilt)
}