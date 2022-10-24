import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
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
    androidMainImplementation(libs.ktor.okhttp)
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:domain:trakt:api"))
    commonMainImplementation(project(":shared:domain:shows:api"))
    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.ktor.serialization)

}
