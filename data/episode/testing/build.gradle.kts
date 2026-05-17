plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.data.upnext.api)
            }
        }

        commonMain {
            dependencies {
                api(projects.data.episode.api)
                api(projects.data.database.sqldelight)
                api(libs.coroutines.core)
            }
        }
    }
}
