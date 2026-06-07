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
                api(projects.core.base)
                api(projects.core.featureFlags.api)
                api(projects.core.logger.api)
                api(projects.core.util.api)
                api(projects.core.view)
                api(projects.data.accountManager.api)
                api(projects.data.episode.api)
                api(projects.data.followedshows.api)
                api(projects.data.watchlistPrefs.api)
                api(projects.domain.episode)
                api(projects.domain.followedshows)
                api(projects.domain.continueWatching)
                api(projects.features.myShows.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.seasonDetails.nav)
                implementation(projects.features.showDetails.nav)
            }
        }

        androidMain {
            dependencies {
                api(projects.core.syncstate.api)
                api(projects.data.database.sqldelight)
                implementation(projects.i18n.generator)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(libs.kotlinx.datetime)
                implementation(projects.core.base.testing)
                implementation(projects.core.featureFlags.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.syncActivity.api)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.upnext.api)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.watchproviders.testing)
                implementation(projects.data.continueWatching.testing)
                implementation(projects.data.watchlistPrefs.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
