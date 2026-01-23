plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
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
                implementation(projects.data.followedshows.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.data.watchlist.api)
                implementation(projects.data.syncActivity.api)

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
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.syncActivity.testing)
            }
        }
    }
}
