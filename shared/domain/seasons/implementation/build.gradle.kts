import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.seasons.implementation"
}

dependencies {
    androidMainImplementation(project(":shared:core:ui"))
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:tmdb:api"))
    commonMainImplementation(projects.shared.domain.seasons.api)
    commonMainImplementation(projects.shared.domain.showDetails.implementation)
    commonMainImplementation(project(":shared:domain:show-common:api"))

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.kotlin.coroutines.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.mockk.common)
    commonTestImplementation(libs.testing.kotest.assertions)
}
