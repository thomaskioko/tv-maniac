plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useSerialization()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.root.nav)
            implementation(projects.core.base)
            implementation(projects.core.logger.api)
            implementation(projects.data.traktauth.api)
            implementation(projects.domain.discover)
            implementation(projects.domain.genre)
            implementation(projects.domain.logout)
            implementation(projects.domain.recommendedshows)
            implementation(projects.domain.seasondetails)
            implementation(projects.domain.showdetails)
            implementation(projects.domain.similarshows)
            implementation(projects.domain.user)
            implementation(projects.domain.watchproviders)
            implementation(projects.navigation.api)
            implementation(projects.features.root.presenter)

            implementation(projects.features.home.nav.api)
            implementation(projects.features.showDetails.nav.api)
            implementation(projects.features.seasonDetails.nav.api)

            implementation(projects.features.calendar.presenter)
            implementation(projects.features.debug.presenter)
            implementation(projects.features.discover.presenter)
            implementation(projects.features.episodeDetail.presenter)
            implementation(projects.features.home.presenter)
            implementation(projects.features.library.presenter)
            implementation(projects.features.moreShows.presenter)
            implementation(projects.features.profile.presenter)
            implementation(projects.features.progress.presenter)
            implementation(projects.features.search.presenter)
            implementation(projects.features.seasonDetails.presenter)
            implementation(projects.features.settings.presenter)
            implementation(projects.features.showDetails.presenter)
            implementation(projects.features.trailers.presenter)
            implementation(projects.features.upnext.presenter)
            implementation(projects.features.watchlist.presenter)
        }

        commonTest.dependencies {
            implementation(projects.core.util.testing)
            implementation(projects.data.traktauth.testing)
            implementation(projects.core.testing.di)

            implementation(libs.bundles.unittest)
        }
    }
}
