plugins {
    id("tvmaniac.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thomaskioko.tvmaniac.workmanager"
}

dependencies {

    implementation(project(":shared:core:base"))
    implementation(project(":shared:data:profile:api"))
    implementation(project(":shared:data:shows:api"))

    implementation(libs.androidx.work.runtime)

    implementation(libs.kermit)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}