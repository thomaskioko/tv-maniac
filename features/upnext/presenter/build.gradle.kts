plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.core.view)
                api(projects.data.episode.api)
                api(projects.data.followedshows.api)
                api(projects.data.traktauth.api)
                api(projects.data.upnext.api)
                api(projects.domain.episode)
                api(projects.domain.followedshows)
                api(projects.domain.continueWatching)
                api(projects.features.progress.nav)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.episodeSheet.nav)
                implementation(projects.features.seasonDetails.nav)
                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.continueWatching.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.watchproviders.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
