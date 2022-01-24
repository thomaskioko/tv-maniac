plugins {
    `kmm-domain-plugin`
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
