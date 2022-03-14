import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasonepisodes.implementation"
}

dependencies {
    commonMainImplementation(projects.shared.remote)
    commonMainImplementation(projects.shared.domain.episodes.api)
    commonMainImplementation(projects.shared.domain.seasonEpisodes.api)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(projects.shared.coreTest)
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.mockk.common)
    commonTestImplementation(libs.testing.kotest.assertions)
}
