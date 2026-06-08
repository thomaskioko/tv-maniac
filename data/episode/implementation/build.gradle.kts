plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()

    optIn("kotlinx.coroutines.DelicateCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(libs.store5)
                api(projects.api.trakt.api)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.core.util.api)
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.datastore.api)
                api(projects.data.episode.api)
                api(projects.data.followedshows.api)
                api(projects.data.requestManager.api)
                api(projects.data.syncActivity.api)
                api(projects.data.upnext.api)
                api(projects.data.watchStatus.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.kotlinx.datetime)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.util.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.watchStatus.testing)
            }
        }
    }
}
