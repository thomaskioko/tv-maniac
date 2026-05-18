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
                api(projects.data.database.sqldelight)
                api(projects.data.requestManager.api)
                api(projects.data.syncActivity.api)
                api(projects.data.watchedShows.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.api.trakt.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.watchedShows.testing)
            }
        }
    }
}
