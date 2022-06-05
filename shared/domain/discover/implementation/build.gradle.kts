import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.discover.implementation"
}

dependencies {
    androidMainImplementation(project(":shared:core:ui"))
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:remote"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:discover:api"))
    commonMainImplementation(project(":shared:domain:show-common:api"))


    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.kotlin.datetime)
    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    testImplementation(libs.testing.mockk.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.mockk.common)

}
