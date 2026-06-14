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
                api(libs.store5)
                api(projects.core.base)
                api(projects.data.accountManager.api)
                api(projects.data.calendar.api)
                api(projects.data.database.sqldelight)
                api(projects.data.requestManager.api)
                api(projects.data.shows.api)
                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
