import util.libs

plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainApi(projects.shared.database)

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
