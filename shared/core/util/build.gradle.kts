import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.thomaskioko.tvmaniac.core.util"
}

dependencies {

    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.datetime)
    commonMainImplementation(libs.coroutines.core)

    iosMainImplementation(libs.koin)
    iosMainImplementation(libs.datetime)
}
