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
                api(projects.data.shows.api)

                implementation(projects.data.database.sqldelight)

                api(libs.coroutines.core)
            }
        }
    }
}
