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

                implementation(projects.api.trakt.api)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.networkUtil.api)
                implementation(projects.core.util.api)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.datastore.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.shows.api)
                implementation(projects.data.upnext.api)

                implementation(libs.kotlinx.datetime)
                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.implementation)
                implementation(projects.data.followedshows.implementation)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.shows.implementation)
            }
        }
    }
}
