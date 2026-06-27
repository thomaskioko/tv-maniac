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
                api(projects.data.database.sqldelight)
                api(projects.data.logout.api)
                api(projects.data.requestManager.api)
                api(projects.data.syncActivity.api)
                api(projects.data.user.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.database.testing)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.user.testing)
            }
        }
    }
}
