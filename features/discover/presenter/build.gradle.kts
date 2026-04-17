plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useCodegen()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                implementation(projects.navigation.api)
                implementation(projects.features.root.nav)
                implementation(projects.features.discover.nav)
                implementation(projects.features.home.nav)
                implementation(projects.features.moreShows.nav)
                implementation(projects.features.search.nav)
                implementation(projects.features.showDetails.nav)
                implementation(projects.features.seasonDetails.nav)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.domain.discover)
                implementation(projects.domain.episode)
                implementation(projects.domain.followedshows)
                implementation(projects.domain.genre)
                implementation(projects.domain.upnext)
                implementation(projects.data.episode.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.traktauth.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.featuredshows.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.genre.testing)
                implementation(projects.data.popularshows.testing)
                implementation(projects.data.topratedshows.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.trendingshows.testing)
                implementation(projects.data.upcomingshows.testing)
                implementation(projects.data.user.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
