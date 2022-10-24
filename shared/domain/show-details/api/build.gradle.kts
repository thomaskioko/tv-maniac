import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.details.api"
}

dependencies {
    commonMainImplementation(project(":shared:core:ui"))
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:domain:seasons:api"))
    commonMainImplementation(project(":shared:domain:last-air-episodes:api"))
    commonMainImplementation(project(":shared:domain:similar:api"))
    commonMainImplementation(project(":shared:domain:shows:api"))
    commonMainImplementation(project(":shared:domain:trailers:api"))
    commonMainImplementation(project(":shared:domain:trakt:api"))

    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.coroutines.core)

}
