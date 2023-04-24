plugins {
    id("tvmaniac.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thomaskioko.tvmaniac.workmanager"
}

dependencies {

    api(projects.shared.core.util)
    api(projects.shared.data.profile.api)
    api(projects.shared.data.shows.api)

    api(libs.androidx.work.runtime)

    implementation(libs.kermit)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}