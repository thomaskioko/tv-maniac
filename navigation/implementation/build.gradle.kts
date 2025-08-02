plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useDependencyInjection()
    useSerialization()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logger.api)
            implementation(projects.data.traktauth.api)
            implementation(projects.domain.discover)
            implementation(projects.domain.genre)
            implementation(projects.domain.recommendedshows)
            implementation(projects.domain.seasondetails)
            implementation(projects.domain.showdetails)
            implementation(projects.domain.similarshows)
            implementation(projects.domain.watchproviders)
            implementation(projects.domain.watchlist)
            implementation(projects.navigation.api)

            implementation(projects.presenter.discover)
            implementation(projects.presenter.home)
            implementation(projects.presenter.moreShows)
            implementation(projects.presenter.search)
            implementation(projects.presenter.seasondetails)
            implementation(projects.presenter.settings)
            implementation(projects.presenter.showDetails)
            implementation(projects.presenter.trailers)
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
