plugins {
    id("tvmaniac.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thomaskioko.tvmaniac.workmanager"
}

dependencies {

    implementation(projects.shared.core.base)
    implementation(projects.shared.data.profile.api)
    implementation(projects.shared.data.shows.api)

    api(libs.androidx.work.runtime)

    implementation(libs.kermit)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}