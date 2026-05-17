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
                api(projects.api.trakt.api)
                api(projects.core.base)
                api(projects.core.util.api)
                api(projects.data.database.sqldelight)
                api(projects.data.shows.api)
                api(projects.data.trailers.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
