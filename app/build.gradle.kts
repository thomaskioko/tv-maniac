import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    alias(libs.plugins.app.application)
}

scaffold {
    app {
        applicationId("com.thomaskioko.tvmaniac")
        minify(
            rootProject.file("app/proguard-rules.pro"),
        )
    }

    android {
        useMetro()
        useCompose()
        useBaselineProfile(projects.benchmark)
        useManagedDevices()
    }
}

dependencies {
    implementation(projects.androidDesignsystem)
    implementation(projects.androidFeature.debug)
    implementation(projects.androidFeature.episodeDetail)
    implementation(projects.androidFeature.home)
    implementation(projects.androidFeature.moreShows)
    implementation(projects.androidFeature.search)
    implementation(projects.androidFeature.settings)
    implementation(projects.androidFeature.seasonDetails)
    implementation(projects.androidFeature.showDetails)
    implementation(projects.androidFeature.trailers)
    implementation(projects.api.tmdb.implementation)
    implementation(projects.api.trakt.implementation)
    implementation(projects.core.appconfig.api)
    implementation(projects.core.appconfig.implementation)
    implementation(projects.core.base)
    implementation(projects.core.util.implementation)
    implementation(projects.core.imageloading.implementation)
    implementation(projects.core.locale.api)
    implementation(projects.core.locale.implementation)
    implementation(projects.core.tasks.implementation)
    implementation(projects.navigation.api)
    implementation(projects.navigation.implementation)
    implementation(projects.core.logger.api)
    implementation(projects.core.logger.implementation)
    implementation(projects.data.cast.implementation)
    implementation(projects.data.episode.implementation)
    implementation(projects.data.featuredshows.api)
    implementation(projects.data.featuredshows.implementation)
    implementation(projects.data.genre.implementation)
    implementation(projects.data.traktauth.api)
    implementation(projects.data.traktauth.implementation)
    implementation(projects.data.popularshows.api)
    implementation(projects.data.popularshows.implementation)
    implementation(projects.data.recommendedshows.implementation)
    implementation(projects.data.requestManager.implementation)
    implementation(projects.data.search.implementation)
    implementation(projects.data.seasondetails.implementation)
    implementation(projects.data.seasons.implementation)
    implementation(projects.data.showdetails.implementation)
    implementation(projects.data.shows.implementation)
    implementation(projects.data.similar.implementation)
    implementation(projects.data.topratedshows.api)
    implementation(projects.data.topratedshows.implementation)
    implementation(projects.data.trailers.implementation)
    implementation(projects.data.trendingshows.api)
    implementation(projects.data.trendingshows.implementation)
    implementation(projects.data.upcomingshows.api)
    implementation(projects.data.upcomingshows.implementation)
    implementation(projects.data.watchproviders.implementation)
    implementation(projects.data.user.implementation)
    implementation(projects.data.database.sqldelight)
    implementation(projects.data.datastore.api)
    implementation(projects.data.datastore.implementation)
    implementation(projects.domain.calendar)
    implementation(projects.domain.discover)
    implementation(projects.domain.episode)
    implementation(projects.domain.followedshows)
    implementation(projects.domain.genre)
    implementation(projects.domain.notifications)
    implementation(projects.domain.seasondetails)
    implementation(projects.domain.showdetails)
    implementation(projects.domain.similarshows)
    implementation(projects.domain.watchproviders)
    implementation(projects.domain.library)
    implementation(projects.domain.settings)
    implementation(projects.domain.upnext)
    implementation(projects.domain.user)
    implementation(projects.domain.logout)
    implementation(projects.presenter.debug)
    implementation(projects.presenter.calendar)
    implementation(projects.presenter.discover)
    implementation(projects.presenter.episodeDetail)
    implementation(projects.presenter.library)
    implementation(projects.presenter.home)
    implementation(projects.presenter.moreShows)
    implementation(projects.presenter.search)
    implementation(projects.presenter.seasondetails)
    implementation(projects.presenter.settings)
    implementation(projects.presenter.profile)
    implementation(projects.presenter.progress)
    implementation(projects.presenter.showDetails)
    implementation(projects.presenter.trailers)
    implementation(projects.presenter.upnext)
    implementation(projects.data.traktlists.implementation)
    implementation(projects.domain.traktlists)
    implementation(projects.core.util.api)
    implementation(projects.core.view)
    implementation(projects.data.calendar.api)
    implementation(projects.data.followedshows.api)
    implementation(projects.data.genre.api)
    implementation(projects.data.user.api)
    implementation(projects.i18n.api)

    implementation(projects.core.networkUtil.implementation)
    implementation(projects.core.notifications.api)
    implementation(projects.core.notifications.implementation)
    implementation(projects.core.connectivity.implementation)
    implementation(projects.core.tasks.api)
    implementation(projects.data.followedshows.implementation)
    implementation(projects.data.library.implementation)
    implementation(projects.data.syncActivity.implementation)
    implementation(projects.data.upnext.implementation)
    implementation(projects.data.watchlist.implementation)
    implementation(projects.i18n.generator)
    implementation(projects.i18n.implementation)
    implementation(projects.data.calendar.implementation)

    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preference)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.work.runtime)
    implementation(libs.appauth)

    implementation(libs.decompose.decompose)
    implementation(libs.decompose.extensions.compose)

    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.savedstate)
    implementation(libs.sqldelight.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.coroutines.core)
    implementation(libs.ktor.core)

    testRuntimeOnly(projects.core.notifications.implementation)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}

if (file("google-services.json").exists()) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
    apply(plugin = libs.plugins.firebase.crashlytics.gradle.get().pluginId)
}

afterEvaluate {
    if (pluginManager.hasPlugin("com.google.firebase.crashlytics")) {
        android.buildTypes.getByName("release") {
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
    }
}
