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
                api(projects.data.database.sqldelight)
                implementation(libs.kotlinx.datetime)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.util.api)
                api(projects.data.library.api)
                api(projects.data.syncActivity.api)
                api(projects.data.upnext.api)
                api(projects.data.watchlist.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.watchlist.testing)
            }
        }
    }
}
