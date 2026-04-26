@file:OptIn(KotlinNativeCacheApi::class)

import org.jetbrains.kotlin.gradle.plugin.mpp.DisableCacheInKotlinVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCacheApi
import java.net.URI

plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()

    addIosTargetsWithXcFramework(
        frameworkName = "TvManiac",
    ) { framework ->
        with(framework) {
            isStatic = true
            if (debuggable) freeCompilerArgs += "-Xadd-light-debug=enable"
            freeCompilerArgs += listOf("-Xbinary=bundleId=Kotlin", "-Xexport-kdoc")

            disableNativeCache(
                version = DisableCacheInKotlinVersion.`2_3_21`,
                reason = "cache bug causes double runtime injection when linking multiple frameworks, see KT-42254",
                issueUrl = URI("https://youtrack.jetbrains.com/issue/KT-42254"),
            )

            export(projects.i18n.api)
            export(projects.core.logger.api)
            export(projects.navigation.api)
            export(projects.domain.theme)
            export(projects.data.traktauth.api)
            export(projects.api.trakt.api)
            export(projects.features.calendar.presenter)
            export(projects.features.discover.presenter)
            export(projects.features.genreShows.presenter)
            export(projects.features.home.nav)
            export(projects.features.home.presenter)
            export(projects.features.watchlist.presenter)
            export(projects.features.moreShows.presenter)
            export(projects.features.search.presenter)
            export(projects.features.seasonDetails.presenter)
            export(projects.features.seasonDetails.nav)
            export(projects.features.settings.presenter)
            export(projects.features.showDetails.nav)
            export(projects.features.showDetails.presenter)
            export(projects.features.trailers.presenter)
            export(projects.features.profile.presenter)
            export(projects.features.progress.presenter)
            export(projects.features.library.presenter)
            export(projects.features.upnext.presenter)
            export(projects.features.debug.presenter)
            export(projects.features.episodeSheet.presenter)
            export(projects.features.root.presenter)
            export(projects.features.root.nav)
            export(projects.core.testTags)
            export(projects.domain.notifications)

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
                api(projects.core.appconfig.api)
                api(projects.core.appconfig.implementation)
                api(projects.api.trakt.api)
                api(projects.core.util.api)
                api(projects.core.networkUtil.api)
                api(projects.core.networkUtil.implementation)
                api(projects.i18n.api)
                api(projects.navigation.api)
                api(projects.data.traktauth.api)
                api(projects.features.calendar.presenter)
                api(projects.features.discover.presenter)
                api(projects.features.genreShows.presenter)
                api(projects.features.watchlist.presenter)
                api(projects.features.home.nav)
                api(projects.features.home.presenter)
                api(projects.features.moreShows.presenter)
                api(projects.features.search.presenter)
                api(projects.features.seasonDetails.presenter)
                api(projects.features.settings.presenter)
                api(projects.features.showDetails.presenter)
                api(projects.features.trailers.presenter)
                api(projects.features.profile.presenter)
                api(projects.features.progress.presenter)
                api(projects.features.watchlist.presenter)
                api(projects.features.library.presenter)
                api(projects.features.upnext.presenter)
                api(projects.features.debug.presenter)
                api(projects.features.episodeSheet.presenter)
                api(projects.features.root.presenter)
                api(projects.features.seasonDetails.nav)
                api(projects.features.showDetails.nav)
                api(projects.features.root.nav)
                api(projects.core.testTags)

                api(projects.domain.calendar)
                api(projects.domain.followedshows)
                api(projects.data.followedshows.api)
                api(projects.data.followedshows.implementation)
                api(projects.data.syncActivity.api)
                api(projects.data.syncActivity.implementation)
                api(projects.core.notifications.api)
                api(projects.core.notifications.implementation)
                api(projects.domain.notifications)
                api(projects.domain.settings)

                api(projects.data.watchlist.api)
                api(projects.data.watchlist.implementation)
                api(projects.data.library.api)
                api(projects.data.library.implementation)
                api(projects.data.upnext.api)
                api(projects.data.upnext.implementation)
                api(projects.domain.upnext)
                api(projects.data.calendar.api)
                implementation(projects.data.calendar.implementation)
                implementation(projects.data.traktlists.api)
                implementation(projects.data.traktlists.implementation)
                implementation(projects.domain.traktlists)

                implementation(projects.api.tmdb.api)
                implementation(projects.api.tmdb.implementation)
                implementation(projects.api.trakt.implementation)
                implementation(projects.core.base)
                implementation(projects.core.locale.api)
                implementation(projects.core.locale.implementation)
                implementation(projects.core.logger.api)
                implementation(projects.core.logger.implementation)
                implementation(projects.core.util.implementation)
                implementation(projects.core.tasks.api)
                implementation(projects.core.tasks.implementation)
                implementation(projects.core.connectivity.api)
                implementation(projects.core.connectivity.implementation)
                implementation(projects.data.cast.api)
                implementation(projects.data.cast.implementation)
                implementation(projects.data.genre.api)
                implementation(projects.data.genre.implementation)
                implementation(projects.data.episode.api)
                implementation(projects.data.episode.implementation)
                implementation(projects.data.featuredshows.api)
                implementation(projects.data.featuredshows.implementation)
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
                implementation(projects.domain.theme)
                implementation(projects.data.datastore.implementation)
                implementation(projects.data.traktauth.api)
                implementation(projects.data.traktauth.implementation)
                implementation(projects.data.user.api)
                implementation(projects.data.user.implementation)
                implementation(projects.domain.user)
                implementation(projects.domain.followedshows)
                implementation(projects.i18n.implementation)

                implementation(projects.navigation.implementation)
            }
        }
    }
}
