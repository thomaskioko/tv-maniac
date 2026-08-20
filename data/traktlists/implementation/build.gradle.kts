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
                api(projects.core.util.api)
                api(projects.core.logger.api)
                api(projects.data.backup.api)
                api(projects.data.database.sqldelight)
                api(projects.data.requestManager.api)
                api(projects.data.traktlists.api)
                api(projects.data.shows.api)

                implementation(projects.core.networkUtil.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.user.api)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.data.shows.testing)
                implementation(projects.data.traktlists.testing)
                implementation(projects.data.user.testing)
            }
        }
    }
}
