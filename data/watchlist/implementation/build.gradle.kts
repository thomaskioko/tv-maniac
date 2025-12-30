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

                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil)
                implementation(projects.core.util.api)
                implementation(projects.core.util)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.datastore.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.watchlist.api)
                implementation(projects.data.seasons.api)
                implementation(projects.data.seasondetails.api)

                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.database.testing)
                implementation(projects.data.datastore.testing)
            }
        }
    }
}
