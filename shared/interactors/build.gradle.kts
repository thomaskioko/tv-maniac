import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.interactors"
}

dependencies {

    commonMainImplementation(projects.shared.core)
    commonMainImplementation(projects.shared.database)
    commonMainImplementation(projects.shared.remote)
    commonMainImplementation(projects.shared.domain.showDetails.api)
    commonMainImplementation(projects.shared.domain.showCommon.api)

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.kotlin.coroutines.core)
}
