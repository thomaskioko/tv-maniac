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
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.view)
                api(projects.data.traktauth.api)
                api(projects.domain.discover)
                api(projects.domain.episode)
                api(projects.domain.followedshows)
                api(projects.domain.genre)
                api(projects.domain.showdetails)
                api(projects.domain.startWatching)
                api(projects.features.discover.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                api(projects.domain.continueWatching)
                implementation(projects.data.episode.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.startWatching.api)
                implementation(projects.features.episodeSheet.nav)
                implementation(projects.features.home.nav)
                implementation(projects.features.moreShows.nav)
                implementation(projects.features.myShows.nav)
                implementation(projects.features.progress.nav)
                implementation(projects.features.search.nav)
                implementation(projects.features.showDetails.nav)
                implementation(projects.features.seasonDetails.nav)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.i18n.generator)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.featuredshows.api)
                implementation(projects.data.featuredshows.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.genre.api)
                implementation(projects.data.genre.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.popularshows.api)
                implementation(projects.data.popularshows.testing)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.shows.api)
                implementation(projects.data.startWatching.testing)
                implementation(projects.data.topratedshows.api)
                implementation(projects.data.topratedshows.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.trendingshows.api)
                implementation(projects.data.trendingshows.testing)
                implementation(projects.data.upcomingshows.api)
                implementation(projects.data.upcomingshows.testing)
                implementation(projects.data.upnext.api)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.watchproviders.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
