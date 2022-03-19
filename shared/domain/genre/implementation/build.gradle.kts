import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.genre.implementation"
}

dependencies {
    commonMainImplementation(projects.shared.remote)
    commonMainImplementation(projects.shared.domain.genre.api)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(projects.shared.core.test)
}
