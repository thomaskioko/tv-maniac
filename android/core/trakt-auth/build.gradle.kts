plugins {
    id("tvmaniac.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth"
}

dependencies {

    implementation(project(":shared:core:base"))
    implementation(project(":android:core:workmanager"))

    implementation(libs.appauth)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.core)
    implementation(libs.androidx.work.runtime)
    implementation(libs.kermit)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}