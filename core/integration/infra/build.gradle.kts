import com.autonomousapps.DependencyAnalysisSubExtension

plugins {
    alias(libs.plugins.app.kmp)
}

// TODO: replace with `scaffold { ignoreUnused(...) }`.
configure<DependencyAnalysisSubExtension> {
    issues {
        onUnusedDependencies {
            exclude(
                ":data:request-manager:testing",
                ":data:sync-activity:testing",
                ":data:traktlists:testing",
                ":data:user:testing",
            )
        }
    }
}

scaffold {
    useMetro()
    addAndroidTarget(
        enableAndroidResources = true,
    )
    android {
        useCompose()
        manifestPlaceholders(
            mapOf("appAuthRedirectScheme" to "com.thomaskioko.tvmaniac.test"),
        )
    }
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        val jvmAndIosMain by creating {
            dependsOn(getByName("commonMain"))
        }
        getByName("jvmMain").dependsOn(jvmAndIosMain)
        getByName("iosMain").dependsOn(jvmAndIosMain)

        commonMain.dependencies {
            api(projects.api.tmdb.api)
            api(projects.api.tmdb.implementation)
            api(projects.api.trakt.api)
            api(projects.api.trakt.implementation)
            api(projects.core.appconfig.api)
            api(projects.core.appconfig.implementation)
            api(projects.core.base)
            api(projects.core.connectivity.api)
            api(projects.core.connectivity.implementation)
            api(projects.core.connectivity.testing)
            api(projects.core.locale.api)
            api(projects.core.locale.testing)
            api(projects.core.logger.api)
            api(projects.core.logger.implementation)
            api(projects.core.logger.testing)
            api(projects.core.networkUtil.implementation)
            api(projects.core.notifications.implementation)
            api(projects.core.notifications.testing)
            api(projects.core.syncstate.api)
            api(projects.core.syncstate.implementation)
            api(projects.core.tasks.implementation)
            api(projects.core.tasks.testing)
            api(projects.core.util.api)
            api(projects.core.util.implementation)
            api(projects.core.util.testing)
            api(projects.data.calendar.implementation)
            api(projects.data.cast.implementation)
            api(projects.data.database.sqldelight)
            api(projects.data.datastore.api)
            api(projects.data.datastore.implementation)
            api(projects.data.episode.implementation)
            api(projects.data.featuredshows.implementation)
            api(projects.data.followedshows.implementation)
            api(projects.data.genre.implementation)
            api(projects.data.library.implementation)
            api(projects.data.popularshows.implementation)
            api(projects.data.search.implementation)
            api(projects.data.seasondetails.implementation)
            api(projects.data.seasons.implementation)
            api(projects.data.showdetails.implementation)
            api(projects.data.shows.implementation)
            api(projects.data.similar.implementation)
            api(projects.data.syncActivity.implementation)
            api(projects.data.topratedshows.implementation)
            api(projects.data.trailers.implementation)
            api(projects.data.traktauth.api)
            api(projects.data.traktauth.implementation)
            api(projects.data.traktauth.testing)
            api(projects.data.traktlists.implementation)
            api(projects.data.trendingshows.implementation)
            api(projects.data.upcomingshows.implementation)
            api(projects.data.upnext.implementation)
            api(projects.data.watchedShows.implementation)
            api(projects.data.user.api)
            api(projects.data.user.implementation)
            api(projects.data.watchlist.implementation)
            api(projects.data.watchproviders.implementation)
            api(projects.domain.episode)
            api(projects.domain.library)
            api(projects.domain.logout)
            api(projects.domain.notifications)
            api(projects.domain.upnext)
            api(projects.domain.user)
            api(projects.domain.watchlist)
            api(projects.features.discover.nav)
            api(projects.features.genreShows.nav)
            api(projects.features.home.nav)
            api(projects.features.home.presenter)
            api(projects.features.library.nav)
            api(projects.features.profile.nav)
            api(projects.features.progress.nav)
            api(projects.features.root.presenter)
            api(projects.i18n.implementation)
            api(projects.navigation.api)
            api(projects.navigation.implementation)

            api(libs.decompose.decompose)
            api(libs.kotlin.test)
            api(libs.kotest.assertions)
            api(libs.coroutines.test)
            api(libs.ktor.mock)
        }

        jvmMain.dependencies {
            api(projects.data.requestManager.testing)
            api(projects.data.syncActivity.testing)
            api(projects.data.traktlists.testing)
            api(projects.data.user.testing)
            implementation(libs.sqldelight.driver.jvm)
        }

        iosMain.dependencies {
            api(projects.data.requestManager.testing)
            api(projects.data.syncActivity.testing)
            api(projects.data.traktlists.testing)
            api(projects.data.user.testing)
        }

        androidMain.dependencies {
            api(libs.ktor.core)
            api(libs.kotlin.test.junit)
            api(projects.core.imageloading.implementation)
            api(projects.core.notifications.api)
            api(projects.core.tasks.api)

            implementation(libs.kotlinx.serialization.json)
        }
    }
}
