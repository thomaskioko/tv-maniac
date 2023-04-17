plugins {
    id("tvmaniac.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth"
}

dependencies {

    implementation(projects.android.core.workmanager)
    implementation(projects.shared.util)

    implementation(libs.appauth)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.core)
    implementation(libs.androidx.work.runtime)
    implementation(libs.kermit)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}