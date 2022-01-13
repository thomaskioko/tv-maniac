plugins {
    `kmm-domain-plugin`
}

dependencies {

    commonMainImplementation(project(":shared:core"))
    commonMainImplementation(project(":shared:database"))
    commonMainImplementation(project(":shared:remote"))
    commonMainImplementation(project(":shared:domain:discover:api"))

    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.kotlin.coroutines.core)
}
