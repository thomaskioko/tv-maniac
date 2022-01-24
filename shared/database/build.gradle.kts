plugins {
    `kmm-domain-plugin`
    id("com.squareup.sqldelight")
}

dependencies {
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.squareup.sqldelight.runtime)

    androidMainImplementation(libs.squareup.sqldelight.driver.android)

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
