import util.libs

plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainApi(project(":shared:core"))
    commonMainApi(project(":shared:domain:genre:api"))
    commonMainApi(project(":shared:domain:seasons:api"))
    commonMainApi(project(":shared:domain:last-air-episodes:api"))
    commonMainImplementation(project(":shared:domain:similar:api"))
    commonMainImplementation(project(":shared:domain:show-common:api"))

    commonMainApi(libs.koin.core)
    commonMainApi(libs.kermit)

    commonMainImplementation(libs.multiplatform.paging.core)
    commonMainImplementation(libs.kotlin.coroutines.core)

    iosMainImplementation(libs.kotlin.coroutines.core)

    val coroutineCore = libs.kotlin.coroutines.core.get()

    @Suppress("UnstableApiUsage")
    iosMainImplementation("${coroutineCore.module.group}:${coroutineCore.module.name}:${coroutineCore.versionConstraint.displayName}") {
        version {
            strictly(libs.versions.coroutines.native.get())
        }
    }
}
