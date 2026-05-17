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
                api(projects.core.base)
                api(projects.data.database.sqldelight)
                api(projects.data.shows.api)

                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
