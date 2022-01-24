plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:remote"))
    commonMainImplementation(project(":shared:domain:last-air-episodes:api"))
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core-test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.mockk.common)
    commonTestImplementation(libs.testing.coroutines.test)
}
