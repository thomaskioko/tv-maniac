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
                api(projects.data.shows.api)
                api(projects.data.database.sqldelight)
                api(libs.androidx.paging.common)
                api(libs.coroutines.core)
            }
        }
    }
}
