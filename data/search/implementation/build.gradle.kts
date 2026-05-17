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
                api(projects.data.search.api)

                implementation(projects.core.networkUtil.api)
                implementation(projects.data.database.sqldelight)
            }
        }
    }
}
