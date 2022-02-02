plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:remote"))
    commonMainImplementation(project(":shared:domain:show:api"))
    commonMainImplementation(project(":shared:domain:episodes:api"))
    commonMainImplementation(project(":shared:domain:last-air-episodes:api"))
    commonMainImplementation(project(":shared:domain:show-common:api"))

    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.kotlin.datetime)
    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core-test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.mockk.common)
}
