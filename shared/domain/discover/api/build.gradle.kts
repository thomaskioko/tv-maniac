import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.discover.api"
}

dependencies {
    commonMainApi(projects.shared.core)
    commonMainApi(projects.shared.domain.genre.api)
    commonMainApi(projects.shared.domain.seasons.api)
    commonMainApi(projects.shared.domain.lastAirEpisodes.api)
    commonMainImplementation(projects.shared.domain.similar.api)
    commonMainImplementation(projects.shared.domain.showCommon.api)

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
