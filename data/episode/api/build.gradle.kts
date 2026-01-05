plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.database.sqldelight)
                api(projects.data.followedshows.api)
                api(projects.data.seasondetails.api)
                api(libs.coroutines.core)
                api(libs.kotlinx.collections)
                api(libs.kotlinx.datetime)
            }
        }
    }
}
