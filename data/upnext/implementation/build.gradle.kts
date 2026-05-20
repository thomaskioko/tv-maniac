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
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.data.datastore.api)
                api(projects.data.episode.api)
                api(projects.data.requestManager.api)
                api(projects.data.seasondetails.api)
                api(projects.data.showdetails.api)
                api(projects.data.upnext.api)
                api(projects.data.continueWatching.api)

                implementation(projects.core.base)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.continueWatching.testing)
            }
        }
    }
}
