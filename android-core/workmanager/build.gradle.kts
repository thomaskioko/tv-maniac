plugins {
    id("tvmaniac.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thomaskioko.tvmaniac.workmanager"
}

dependencies {

    api(libs.androidx.work.runtime)

    implementation(projects.core.util)
    implementation(projects.data.profile.api)
    implementation(projects.data.shows.api)
    implementation(projects.data.watchlist.api)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}