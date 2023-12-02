plugins {
    id("plugin.tvmaniac.multiplatform")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                api(projects.core.datastore.api)
                api(projects.core.traktAuth.api)
                api(projects.core.util)

                api(projects.navigation)

                api(projects.core.database)
                api(projects.core.datastore.implementation)
                api(projects.data.episodeimages.api)
                api(projects.data.library.api)
                api(projects.core.util)
                api(projects.data.showimages.api)
                api(projects.core.traktApi.api)
                api(projects.core.traktApi.implementation)
                api(projects.core.traktAuth.implementation)
                api(projects.core.tmdbApi.api)
                api(projects.core.tmdbApi.implementation)


                api(projects.data.category.implementation)
                api(projects.data.episodes.implementation)
                api(projects.data.episodeimages.implementation)
                api(projects.data.library.implementation)
                api(projects.data.profile.implementation)
                api(projects.data.profilestats.implementation)
                api(projects.data.similar.implementation)
                api(projects.data.seasons.implementation)
                api(projects.data.seasondetails.implementation)
                api(projects.data.shows.implementation)
                api(projects.data.showimages.implementation)
                api(projects.data.trailers.implementation)
                api(projects.data.requestManager.api)
                api(projects.data.requestManager.implementation)

                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

multiplatformSwiftPackage {
    packageName("TvManiac")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }

    distributionMode { local() }
    outputDirectory(File("$projectDir/../../", "tvmaniac-swift-packages"))
}
