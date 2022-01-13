plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:core"))
    commonMainImplementation(project(":shared:database"))
    commonMainImplementation(project(":shared:remote"))
    commonMainImplementation(project(":shared:domain:seasons:api"))
    commonMainImplementation(project(":shared:domain:discover:api"))

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.kotlin.coroutines.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core-test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.mockk.common)
    commonTestImplementation(libs.testing.kotest.assertions)
}
