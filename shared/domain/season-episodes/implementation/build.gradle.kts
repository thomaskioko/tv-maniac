import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.seasonepisodes.implementation"
}

dependencies {
    commonMainImplementation(projects.shared.database)
    commonMainImplementation(projects.shared.remote)
    commonMainImplementation(projects.shared.domain.episodes.api)
    commonMainImplementation(projects.shared.domain.seasonEpisodes.api)
    commonMainImplementation(projects.shared.domain.seasons.api)

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(projects.shared.core.test)
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.mockk.common)
    commonTestImplementation(libs.testing.kotest.assertions)
}
