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
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                api(projects.core.syncstate.api)
                api(projects.core.util.api)
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.followedshows.api)
                api(projects.data.ratings.api)
                api(projects.data.requestManager.api)
                api(projects.data.shows.api)

                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.ratings.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.shows.implementation)
                implementation(projects.data.shows.testing)
            }
        }
    }
}
