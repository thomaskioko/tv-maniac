import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.lastairepisodes.implementation"
}

dependencies {
    androidMainImplementation(project(":shared:core:ui"))
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:core:remote"))
    commonMainImplementation(project(":shared:domain:last-air-episodes:api"))
    commonMainImplementation(libs.squareup.sqldelight.extensions)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.mockk.common)
    commonTestImplementation(libs.testing.coroutines.test)
}
