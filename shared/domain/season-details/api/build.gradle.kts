import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.seasonepisodes.api"
}

dependencies {
    commonMainApi(project(":shared:core:util"))
    commonMainApi(project(":shared:core:database"))
    commonMainApi(project(":shared:domain:trakt:api"))
    commonMainApi(project(":shared:domain:shows:api"))
    commonMainApi(project(":shared:domain:episodes:api"))

    commonMainApi(libs.flowredux)

    commonMainImplementation(project(":shared:core:ui"))
    commonMainImplementation(libs.coroutines.core)

    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(project(":shared:domain:trakt:testing"))
    commonTestImplementation(project(":shared:domain:tmdb:testing"))
    commonTestImplementation(kotlin("test"))

    commonMainImplementation(libs.kermit)
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.kotest.assertions)
}
