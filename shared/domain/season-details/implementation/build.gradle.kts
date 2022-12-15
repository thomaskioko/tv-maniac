import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.seasonepisodes.implementation"
}

dependencies {
    androidMainImplementation(project(":shared:core:util"))
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:tmdb:api"))
    commonMainImplementation(project(":shared:domain:trakt:api"))
    commonMainImplementation(project(":shared:domain:season-details:api"))

    commonMainImplementation(libs.squareup.sqldelight.extensions)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.mockk.common)
    commonTestImplementation(libs.testing.kotest.assertions)
}
