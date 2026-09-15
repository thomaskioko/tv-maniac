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
                api(projects.core.util.api)
                api(projects.data.accountManager.api)
                api(projects.data.requestManager.api)
                api(projects.data.search.api)

                implementation(projects.core.networkUtil.api)
                api(projects.data.database.sqldelight)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.api.tmdb.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.search.testing)
                implementation(projects.data.shows.implementation)
            }
        }
    }
}
