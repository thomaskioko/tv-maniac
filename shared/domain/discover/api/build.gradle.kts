import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.discover.api"
}

dependencies {
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:core:ui"))
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:domain:show-common:api"))

    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.coroutines.core)

}
