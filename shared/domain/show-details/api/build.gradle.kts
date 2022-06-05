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
    commonMainImplementation(project(":shared:domain:genre:api"))
    commonMainImplementation(project(":shared:domain:seasons:api"))
    commonMainImplementation(project(":shared:domain:last-air-episodes:api"))
    commonMainImplementation(project(":shared:domain:similar:api"))
    commonMainImplementation(project(":shared:domain:show-common:api"))

    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.kotlin.coroutines.core)

}
