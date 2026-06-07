plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.upnext.api)
                api(projects.data.followedshows.api)
                api(libs.coroutines.core)
            }
        }
    }
}
