plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {

    useKotlinInject()
    useSkies()

    addIosTargetsWithXcFramework(
        frameworkName = "TvManiac",
    ) { framework ->
        with(framework) {
            isStatic = true
            freeCompilerArgs += if (debuggable) "-Xadd-light-debug=enable" else ""
            freeCompilerArgs += listOf("-Xbinary=bundleId=Kotlin", "-Xexport-kdoc")

            export(projects.i18n.api)
            export(projects.navigation.api)
            export(projects.data.datastore.api)
            export(projects.data.traktauth.api)
            export(projects.presenter.discover)
            export(projects.presenter.home)
            export(projects.presenter.watchlist)
            export(projects.presenter.moreShows)
            export(projects.presenter.search)
            export(projects.presenter.seasondetails)
            export(projects.presenter.settings)
            export(projects.presenter.showDetails)
            export(projects.presenter.trailers)

            export(libs.decompose.decompose)
            export(libs.essenty.lifecycle)
        }
    }

    optIn("kotlin.experimental.ExperimentalObjCName")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.i18n.api)
                api(projects.navigation.api)
                api(projects.presenter.discover)
                api(projects.presenter.watchlist)
                api(projects.presenter.home)
                api(projects.presenter.moreShows)
                api(projects.presenter.search)
                api(projects.presenter.seasondetails)
                api(projects.presenter.settings)
                api(projects.presenter.showDetails)
                api(projects.presenter.trailers)

                implementation(projects.api.tmdb.api)
                implementation(projects.api.tmdb.implementation)
                implementation(projects.api.trakt.api)
                implementation(projects.api.trakt.implementation)
                implementation(projects.core.base)
                implementation(projects.core.locale.api)
                implementation(projects.core.locale.implementation)
                implementation(projects.core.logger.api)
                implementation(projects.core.logger.implementation)
                implementation(projects.data.cast.api)
                implementation(projects.data.cast.implementation)
                implementation(projects.data.genre.api)
                implementation(projects.data.genre.implementation)
                implementation(projects.data.episode.api)
                implementation(projects.data.episode.implementation)
                implementation(projects.data.featuredshows.api)
                implementation(projects.data.featuredshows.implementation)
                implementation(projects.data.genre.api)
                implementation(projects.data.genre.implementation)
                implementation(projects.data.watchlist.api)
                implementation(projects.data.watchlist.implementation)
                implementation(projects.data.popularshows.api)
                implementation(projects.data.popularshows.implementation)
                implementation(projects.data.recommendedshows.api)
                implementation(projects.data.recommendedshows.implementation)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.requestManager.implementation)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.seasondetails.implementation)
                implementation(projects.data.search.api)
                implementation(projects.data.search.implementation)
                implementation(projects.data.seasons.api)
                implementation(projects.data.seasons.implementation)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.showdetails.implementation)
                implementation(projects.data.shows.api)
                implementation(projects.data.shows.implementation)
                implementation(projects.data.similar.api)
                implementation(projects.data.similar.implementation)
                implementation(projects.data.topratedshows.api)
                implementation(projects.data.topratedshows.implementation)
                implementation(projects.data.trailers.api)
                implementation(projects.data.trailers.implementation)
                implementation(projects.data.trendingshows.api)
                implementation(projects.data.trendingshows.implementation)
                implementation(projects.data.upcomingshows.api)
                implementation(projects.data.upcomingshows.implementation)
                implementation(projects.data.watchproviders.api)
                implementation(projects.data.watchproviders.implementation)
                implementation(projects.data.datastore.api)
                implementation(projects.data.datastore.implementation)
                implementation(projects.data.traktauth.api)
                implementation(projects.data.traktauth.implementation)
                implementation(projects.i18n.implementation)

                implementation(projects.navigation.implementation)
            }
        }
    }
}
