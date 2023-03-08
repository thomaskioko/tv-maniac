import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import util.libs

plugins {
    `kmm-domain-plugin`
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version ("1.6.10")
    kotlin("kapt")
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.thomaskioko.tvmaniac.core.util"
}

dependencies {
    commonMainApi(libs.ktor.serialization)
    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.datetime)
    commonMainImplementation(libs.coroutines.core)

    androidMainImplementation(libs.datetime)
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    iosMainImplementation(libs.koin)
    iosMainImplementation(libs.datetime)
}
