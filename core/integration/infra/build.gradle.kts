plugins {
    alias(libs.plugins.app.kmp)
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
    ignoreUnusedDependencies(
        ":data:request-manager:testing",
        ":data:sync-activity:testing",
        ":data:traktlists:testing",
        ":data:user:testing",
    )
}

kotlin {
    sourceSets {
        val jvmAndIosMain =
            create("jvmAndIosMain") {
                dependsOn(getByName("commonMain"))
            }
        getByName("jvmMain").dependsOn(jvmAndIosMain)
        getByName("iosMain").dependsOn(jvmAndIosMain)

        commonMain.dependencies {
            api(projects.api.simkl.implementation)
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
            api(projects.data.accountManager.implementation)
            api(projects.data.database.sqldelight)
            api(projects.data.datastore.api)
            api(projects.data.datastore.implementation)
            api(projects.data.episode.implementation)
            api(projects.data.followedshows.implementation)
            api(projects.data.library.implementation)
            api(projects.data.requestManager.implementation)
            api(projects.data.seasondetails.implementation)
            api(projects.data.seasons.implementation)
            api(projects.data.showdetails.implementation)
            api(projects.data.shows.implementation)
            api(projects.data.syncActivity.implementation)
            api(projects.data.traktauth.implementation)
            api(projects.data.traktauth.testing)
            api(projects.data.oauth.implementation)
            api(projects.data.oauth.testing)
            api(projects.data.simklauth.implementation)
            api(projects.core.featureFlags.api)
            api(projects.core.featureFlags.implementation)
            api(projects.core.featureFlags.testing)
            api(projects.data.continueWatching.implementation)
            api(projects.data.startWatching.implementation)
            api(projects.data.user.api)
            api(projects.data.user.implementation)
            api(projects.data.watchStatus.implementation)
            api(projects.data.watchproviders.implementation)
            api(projects.domain.episode)
            api(projects.domain.library)
            api(projects.data.logout.implementation)
            api(projects.domain.logout)
            api(projects.domain.notifications)
            api(projects.domain.continueWatching)
            api(projects.domain.user)
            api(projects.domain.continueWatching)
            api(projects.features.discover.nav)
            api(projects.features.home.nav)
            api(projects.features.home.presenter)
            api(projects.features.library.nav)
            api(projects.features.profile.nav)
            api(projects.features.progress.nav)
            api(projects.features.root.presenter)
            api(projects.features.myShows.nav)
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
            implementation(projects.data.syncActivity.testing)
            implementation(projects.data.traktlists.testing)
            implementation(projects.data.user.testing)
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
            api(projects.data.oauth.api)

            implementation(libs.androidx.compose.runtime)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
