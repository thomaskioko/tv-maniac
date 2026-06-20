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
            }
        }

        commonMain {
            dependencies {
                api(libs.store5)
                api(projects.api.tmdb.api)
                api(projects.api.trakt.api)
                api(projects.core.base)
                api(projects.core.util.api)
                api(projects.data.recommendedshows.api)
                api(projects.data.requestManager.api)
                api(projects.data.shows.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.shows.implementation)
            }
        }
    }
}
