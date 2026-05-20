plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(projects.data.database.sqldelight)
                implementation(libs.kotlinx.datetime)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.featureFlags.api)
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.core.tasks.api)
                api(projects.core.util.api)
                api(projects.data.datastore.api)
                api(projects.data.episode.api)
                api(projects.data.seasondetails.api)
                api(projects.data.showdetails.api)
                api(projects.data.syncActivity.api)
                api(projects.data.traktauth.api)
                api(projects.data.upnext.api)
                api(projects.data.continueWatching.api)
                api(projects.data.watchlist.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.episode.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.continueWatching.testing)
                implementation(projects.data.watchlist.testing)
            }
        }
    }
}
