import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.details.implementation"
}

dependencies {
    androidMainImplementation(project(":shared:core:util"))
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project( ":shared:domain:show-details:api"))
    commonMainImplementation(project(":shared:domain:episodes:api"))
    commonMainImplementation(project(":shared:domain:shows:api"))
    commonMainImplementation(project(":shared:domain:similar:api"))
    commonMainImplementation(project(":shared:domain:season-details:api"))
    commonMainImplementation(project(":shared:domain:trailers:api"))
    commonMainImplementation(project(":shared:domain:trakt:api"))

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.datetime)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.mockk.common)
}
