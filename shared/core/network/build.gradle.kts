
import util.libs
import java.util.*

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.remote"
}

dependencies {
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.ktor.logging)
    commonMainImplementation(libs.koin.core)
    commonMainApi(libs.ktor.serialization)
    commonMainApi(libs.ktor.serialization.json)
    commonMainApi(libs.kermit)

    androidMainImplementation(project(":shared:core:util"))

    androidMainApi(libs.ktor.okhttp)
    androidMainApi(libs.ktor.negotiation)
    androidMainApi(libs.ktor.logging)
    androidMainImplementation(libs.squareup.sqldelight.driver.android)
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    iosMainApi(libs.ktor.serialization.json)
    iosMainApi(libs.kermit)
}