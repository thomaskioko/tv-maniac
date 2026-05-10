plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()

    optIn("kotlinx.coroutines.DelicateCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)

                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.upnext.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.implementation)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.implementation)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
            }
        }
    }
}
