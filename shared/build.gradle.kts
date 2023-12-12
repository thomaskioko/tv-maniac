import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets

plugins {
    id("plugin.tvmaniac.kotlin.android")
    id("org.jetbrains.kotlin.multiplatform")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    id("co.touchlab.skie") version "0.5.6"
    alias(libs.plugins.ksp)
}

version = libs.versions.shared.module.version.get()

kotlin {

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "TvManiac"
            isStatic = true
            linkerOpts.add("-lsqlite3")
            freeCompilerArgs += "-Xadd-light-debug=enable"

            export(projects.navigation)
            export(projects.core.datastore.api)
            export(projects.presentation.discover)
            export(projects.presentation.library)
            export(projects.presentation.moreShows)
            export(projects.presentation.profile)
            export(projects.presentation.search)
            export(projects.presentation.seasondetails)
            export(projects.presentation.settings)
            export(projects.presentation.showDetails)
            export(projects.presentation.trailers)

            export(libs.decompose.decompose)
            export(libs.essenty.lifecycle)
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {

                api(projects.core.util)
                api(projects.navigation)

                api(projects.presentation.discover)
                api(projects.presentation.library)
                api(projects.presentation.moreShows)
                api(projects.presentation.profile)
                api(projects.presentation.search)
                api(projects.presentation.seasondetails)
                api(projects.presentation.settings)
                api(projects.presentation.showDetails)
                api(projects.presentation.trailers)

                api(projects.core.database)
                api(projects.core.datastore.api)
                api(projects.core.datastore.implementation)
                api(projects.core.tmdbApi.api)
                api(projects.core.tmdbApi.implementation)
                api(projects.core.traktApi.api)
                api(projects.core.traktApi.implementation)
                api(projects.core.traktAuth.api)
                api(projects.core.traktAuth.implementation)

                api(projects.data.category.api)
                api(projects.data.category.implementation)
                api(projects.data.episodeimages.api)
                api(projects.data.episodeimages.implementation)
                api(projects.data.episodes.api)
                api(projects.data.episodes.implementation)
                api(projects.data.library.api)
                api(projects.data.library.implementation)
                api(projects.data.profile.api)
                api(projects.data.profile.implementation)
                api(projects.data.profilestats.api)
                api(projects.data.profilestats.implementation)
                api(projects.data.requestManager.api)
                api(projects.data.requestManager.api)
                api(projects.data.requestManager.implementation)
                api(projects.data.seasondetails.api)
                api(projects.data.seasondetails.implementation)
                api(projects.data.seasons.api)
                api(projects.data.seasons.implementation)
                api(projects.data.showimages.api)
                api(projects.data.showimages.implementation)
                api(projects.data.shows.api)
                api(projects.data.shows.implementation)
                api(projects.data.similar.api)
                api(projects.data.similar.implementation)
                api(projects.data.topratedshows.api)
                api(projects.data.topratedshows.implementation)
                api(projects.data.trailers.api)
                api(projects.data.trailers.implementation)
                api(projects.data.trendingshows.api)
                api(projects.data.trendingshows.implementation)
                api(projects.data.upcomingshows.api)
                api(projects.data.upcomingshows.implementation)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
            }
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared"
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

addKspDependencyForAllTargets(libs.kotlinInject.compiler)

multiplatformSwiftPackage {
    packageName("TvManiac")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }

    distributionMode { local() }
    outputDirectory(File("$projectDir/../../", "tvmaniac-swift-packages"))
}
