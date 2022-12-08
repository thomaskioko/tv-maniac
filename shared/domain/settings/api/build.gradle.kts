import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.settings.api"
}

dependencies {
    commonMainApi(project(":shared:core:ui"))
    commonMainApi(project(":shared:core:database"))
    commonMainApi(libs.flowredux)

    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(libs.coroutines.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))

    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.turbine)
}
