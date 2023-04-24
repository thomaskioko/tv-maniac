plugins {
    id("tvmaniac.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth"
}

dependencies {

    api(projects.android.core.workmanager)
    api(projects.shared.core.util)

    api(libs.appauth)
    api(libs.coroutines.core)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.work.runtime)
    implementation(libs.kermit)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)

}