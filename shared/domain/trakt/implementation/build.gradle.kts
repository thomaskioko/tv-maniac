import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.trakt.auth.implementation"
}

dependencies {

    androidMainImplementation(project(":shared:core:ui"))
    androidMainImplementation(project(":shared:core:network"))
    androidMainImplementation(libs.appauth)
    androidMainImplementation(libs.kermit)
    androidMainImplementation(libs.androidx.core)
    androidMainImplementation(libs.ktor.okhttp)
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:domain:trakt:api"))
    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.ktor.serialization)

}
