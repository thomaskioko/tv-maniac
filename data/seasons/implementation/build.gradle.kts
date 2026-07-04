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
                api(projects.core.base)
                api(projects.data.database.sqldelight)
                api(projects.data.datastore.api)
                api(projects.data.seasons.api)

                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
