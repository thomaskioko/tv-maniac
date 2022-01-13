plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:core"))
    commonMainImplementation(project(":shared:database"))
    commonMainImplementation(libs.koin.core)
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
