import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.show_common.api"
}

dependencies {
    commonMainApi(project(":shared:core:util"))
    commonMainApi(project(":shared:core:database"))

    commonMainImplementation(project(":shared:domain:trakt:api"))

    commonMainImplementation(libs.coroutines.core)
    commonMainApi(libs.flowredux)

    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(project(":shared:domain:trakt:testing"))
    commonTestImplementation(kotlin("test"))

    commonMainImplementation(libs.kermit)
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.kotest.assertions)

}
