import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.discover.api"
}

dependencies {
    commonMainImplementation(projects.shared.database)
    commonMainImplementation(projects.shared.database)
    commonMainImplementation(projects.shared.core.ui)
    commonMainImplementation(projects.shared.core.util)
    commonMainImplementation(projects.shared.domain.showCommon.api)

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
