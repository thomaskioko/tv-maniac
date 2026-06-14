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
                api(projects.api.tmdb.api)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                api(projects.core.util.api)
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.followedshows.api)
                api(projects.data.requestManager.api)
                api(projects.data.shows.api)
                api(projects.data.startWatching.api)

                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.api.tmdb.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.followedshows.implementation)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.shows.implementation)
            }
        }
    }
}
