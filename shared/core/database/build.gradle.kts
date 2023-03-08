import util.libs

plugins {
    `kmm-domain-plugin`
    id("com.squareup.sqldelight")
}

android {
    namespace = "com.thomaskioko.tvmaniac.core.db"
}

dependencies {
    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.squareup.sqldelight.runtime)

    androidMainImplementation(libs.squareup.sqldelight.driver.android)
    iosMainImplementation(libs.koin)
    iosMainImplementation(libs.squareup.sqldelight.driver.native)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.testing.kotest.assertions)

    androidTestImplementation(kotlin("test"))
    androidTestImplementation(libs.squareup.sqldelight.driver.jvm)
}

sqldelight {
    database("TvManiacDatabase") {
        packageName = "com.thomaskioko.tvmaniac.core.db"
        sourceFolders = listOf("sqldelight")
    }
}
