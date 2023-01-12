import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.episodes.implementation"
}

dependencies {
    androidMainImplementation(project(":shared:core:util"))

    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:tmdb:api"))
    commonMainImplementation(project(":shared:domain:episodes:api"))
    commonMainImplementation(project(":shared:domain:trakt:api"))

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.mockk.common)
}
