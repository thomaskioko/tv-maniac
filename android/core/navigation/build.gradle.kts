plugins {
    id("tvmaniac.compose.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thomaskioko.tvmaniac.navigation"
}

dependencies {

    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.accompanist.navigation.material)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)

    runtimeOnly(libs.coroutines.android)
}
