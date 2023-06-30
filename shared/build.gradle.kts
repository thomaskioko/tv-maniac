plugins {
    id("tvmaniac.kmm.library")
    id("maven-publish")
    alias(libs.plugins.kmmbridge)
    alias(libs.plugins.ksp)
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

version = libs.versions.shared.module.version.get()

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "TvManiac"
            isStatic = false
            linkerOpts.add("-lsqlite3")

            export(projects.core.datastore.api)
            export(projects.core.traktAuth.api)
            export(projects.core.util)
            export(projects.presentation.discover)
            export(projects.presentation.profile)
            export(projects.presentation.seasondetails)
            export(projects.presentation.settings)
            export(projects.presentation.showDetails)
            export(projects.presentation.trailers)
            export(projects.presentation.watchlist)
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {

                api(projects.core.datastore.api)
                api(projects.core.traktAuth.api)
                api(projects.core.util)

                api(projects.presentation.discover)
                api(projects.presentation.profile)
                api(projects.presentation.seasondetails)
                api(projects.presentation.settings)
                api(projects.presentation.seasondetails)
                api(projects.presentation.showDetails)
                api(projects.presentation.trailers)
                api(projects.presentation.watchlist)

                implementation(projects.core.database)
                implementation(projects.core.datastore.implementation)
                implementation(projects.data.episodeimages.api)
                implementation(projects.data.watchlist.api)
                implementation(projects.core.networkutil)
                implementation(projects.data.showimages.api)
                implementation(projects.core.traktApi.api)
                implementation(projects.core.traktApi.implementation)
                implementation(projects.core.traktAuth.implementation)
                implementation(projects.core.tmdbApi.api)
                implementation(projects.core.tmdbApi.implementation)


                implementation(projects.data.category.implementation)
                implementation(projects.data.episodes.implementation)
                implementation(projects.data.episodeimages.implementation)
                implementation(projects.data.watchlist.implementation)
                implementation(projects.data.profile.implementation)
                implementation(projects.data.profilestats.implementation)
                implementation(projects.data.similar.implementation)
                implementation(projects.data.seasons.implementation)
                implementation(projects.data.seasondetails.implementation)
                implementation(projects.data.shows.implementation)
                implementation(projects.data.showimages.implementation)
                implementation(projects.data.trailers.implementation)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.requestManager.implementation)

                implementation(libs.kotlinInject.runtime)
            }
        }

        val commonTest by getting
        val androidMain by getting
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.base"
}

addGithubPackagesRepository()

kmmbridge {
    frameworkName.set("TvManiac")
    mavenPublishArtifacts()
    manualVersions()
    spm()
    versionPrefix.set("0.0.1")
}

//TODO:: Get rid of this once we fully migrate to kmmbridge
multiplatformSwiftPackage {
    packageName("TvManiac")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }

    distributionMode { local() }
    outputDirectory(File("$projectDir/../../", "tvmaniac-swift-packages"))
}
