import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.domain.following.api"
}

dependencies {
    commonMainApi(libs.flowredux)

    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:trakt:api"))

    commonMainImplementation(libs.coroutines.core)
    commonMainImplementation(libs.koin)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:domain:trakt:testing"))

    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.turbine)
}
