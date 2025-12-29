plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil)

                api(projects.data.database.sqldelight)
                implementation(projects.data.shows.api)

                api(libs.coroutines.core)
            }
        }
    }
}
