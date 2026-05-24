plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
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
                api(projects.data.database.sqldelight)
                api(projects.data.datastore.api)
                api(projects.data.followedshows.api)
                api(projects.data.library.api)
                api(projects.data.requestManager.api)
                api(projects.data.syncActivity.api)
                api(projects.data.traktauth.api)
                api(projects.data.watchproviders.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
