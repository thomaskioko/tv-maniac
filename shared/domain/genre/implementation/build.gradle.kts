import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.genre.implementation"
}

dependencies {
    androidMainImplementation(project(":shared:core:ui"))
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:tmdb:api"))
    commonMainImplementation(project(":shared:domain:genre:api"))

    commonMainImplementation(libs.squareup.sqldelight.extensions)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))
}
