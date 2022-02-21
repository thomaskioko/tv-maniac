plugins {
    `kmm-domain-plugin`
}

dependencies {

    commonMainImplementation(projects.shared.core)
    commonMainImplementation(projects.shared.database)
    commonMainImplementation(projects.shared.remote)
    commonMainImplementation(projects.shared.domain.show.api)
    commonMainImplementation(projects.shared.domain.showCommon.api)

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.kotlin.coroutines.core)
}
