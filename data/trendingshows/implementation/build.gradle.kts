plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()

    optIn(
        "androidx.paging.ExperimentalPagingApi",
    )
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(projects.data.shows.api)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(libs.store5)
                api(projects.api.tmdb.api)
                api(projects.api.trakt.api)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.paging)
                api(projects.core.util.api)
                api(projects.data.database.sqldelight)
                api(projects.data.requestManager.api)
                api(projects.data.trendingshows.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.database.testing)
                implementation(projects.data.shows.api)
            }
        }
    }
}
