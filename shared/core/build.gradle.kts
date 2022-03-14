import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.thomaskioko.tvmaniac.shared.core"
}

dependencies {

    commonMainImplementation(libs.kotlin.coroutines.core)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.kotlin.datetime)

    iosMainImplementation(libs.kotlin.coroutines.core)
    iosMainImplementation(libs.koin.core)
    iosMainImplementation(libs.kotlin.datetime)
}
