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
                api(libs.store5)
                api(projects.api.tmdb.api)
                api(projects.api.trakt.api)
                api(projects.core.base)
                api(projects.core.util.api)
                api(projects.data.cast.api)
                api(projects.data.database.sqldelight)
                api(projects.data.datastore.api)
                api(projects.data.episode.api)
                api(projects.data.requestManager.api)
                api(projects.data.seasondetails.api)
                api(projects.data.seasons.api)
                api(projects.data.shows.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
            }
        }
    }
}
