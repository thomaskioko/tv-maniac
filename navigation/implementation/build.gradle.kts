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
            implementation(projects.features.nav)
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

            implementation(projects.presenter.calendar)
            implementation(projects.presenter.debug)
            implementation(projects.presenter.discover)
            implementation(projects.presenter.episodeDetail)
            implementation(projects.presenter.home)
            implementation(projects.presenter.library)
            implementation(projects.presenter.moreShows)
            implementation(projects.presenter.profile)
            implementation(projects.presenter.progress)
            implementation(projects.presenter.search)
            implementation(projects.presenter.seasondetails)
            implementation(projects.presenter.settings)
            implementation(projects.presenter.showDetails)
            implementation(projects.presenter.trailers)
            implementation(projects.presenter.upnext)
            implementation(projects.presenter.watchlist)
        }

        commonTest.dependencies {
            implementation(projects.core.util.testing)
            implementation(projects.data.traktauth.testing)
            implementation(projects.core.testing.di)

            implementation(libs.bundles.unittest)
        }
    }
}
