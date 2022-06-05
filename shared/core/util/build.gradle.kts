import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.thomaskioko.tvmaniac.core.util"
}

dependencies {

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.kotlin.datetime)
    commonMainImplementation(libs.kotlin.coroutines.core)

    iosMainImplementation(libs.koin.core)
    iosMainImplementation(libs.kotlin.datetime)
}
