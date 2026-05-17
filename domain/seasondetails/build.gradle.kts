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
                implementation(projects.data.database.sqldelight)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.data.cast.api)
                api(projects.data.episode.api)
                api(projects.data.seasondetails.api)
            }
        }
    }
}
