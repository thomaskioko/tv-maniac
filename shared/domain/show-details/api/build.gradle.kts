import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.details.api"
}

dependencies {

    commonMainApi(libs.flowredux)
    commonMainApi(project(":shared:core:database"))

    commonMainImplementation(project(":shared:core:ui"))
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:domain:seasons:api"))
    commonMainImplementation(project(":shared:domain:similar:api"))
    commonMainImplementation(project(":shared:domain:trailers:api"))
    commonMainImplementation(project(":shared:domain:trakt:api"))

    commonMainImplementation(libs.coroutines.core)
    commonMainImplementation(libs.kermit)

    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(project(":shared:domain:trakt:testing"))
    commonTestImplementation(project(":shared:domain:similar:testing"))
    commonTestImplementation(project(":shared:domain:trailers:testing"))
    commonTestImplementation(kotlin("test"))

    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.kotest.assertions)

}
