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

                api(projects.presentation.discover)
                api(projects.presentation.profile)
                api(projects.presentation.seasondetails)
                api(projects.presentation.settings)
                api(projects.presentation.seasondetails)
                api(projects.presentation.showDetails)
                api(projects.presentation.trailers)
                api(projects.presentation.library)

                api(projects.common.voyagerutil)

                implementation(projects.core.database)
                implementation(projects.core.datastore.implementation)
                implementation(projects.data.episodeimages.api)
                implementation(projects.data.library.api)
                implementation(projects.core.util)
                implementation(projects.data.showimages.api)
                implementation(projects.core.traktApi.api)
                implementation(projects.core.traktApi.implementation)
                implementation(projects.core.traktAuth.implementation)
                implementation(projects.core.tmdbApi.api)
                implementation(projects.core.tmdbApi.implementation)


                implementation(projects.data.category.implementation)
                implementation(projects.data.episodes.implementation)
                implementation(projects.data.episodeimages.implementation)
                implementation(projects.data.library.implementation)
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
