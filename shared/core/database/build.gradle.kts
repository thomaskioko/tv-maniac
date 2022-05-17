import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("com.squareup.sqldelight")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.core.db"
}

dependencies {
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.squareup.sqldelight.runtime)

    androidMainImplementation(libs.squareup.sqldelight.driver.android)
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    iosMainImplementation(libs.koin.core)
    iosMainImplementation(libs.squareup.sqldelight.driver.native)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.testing.kotest.assertions)
    androidTestImplementation(kotlin("test"))
    androidTestImplementation(libs.squareup.sqldelight.driver.jvm)
}

sqldelight {
    database("TvManiacDatabase") {
        packageName = "com.thomaskioko.tvmaniac.datasource.cache"
        sourceFolders = listOf("sqldelight")
    }
}
