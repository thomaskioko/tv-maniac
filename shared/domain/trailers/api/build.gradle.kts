import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.trailers.api"
}

dependencies {
    commonMainApi(project(":shared:core:util"))
    commonMainApi(project(":shared:core:database"))
    commonMainApi(libs.flowredux)

    commonMainImplementation(libs.coroutines.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(project(":shared:domain:trailers:testing"))

    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.turbine)
}
