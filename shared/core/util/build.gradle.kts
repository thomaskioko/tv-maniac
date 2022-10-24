import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")
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

    iosMainImplementation(libs.koin)
    iosMainImplementation(libs.datetime)
}
