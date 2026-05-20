plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.featureFlags.api)
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.core.tasks.api)
                api(projects.core.util.api)
                api(projects.data.continueWatching.api)
                api(projects.data.datastore.api)
                api(projects.data.traktauth.api)
                api(projects.data.upnext.api)
                api(projects.domain.showdetails)
                api(projects.domain.syncActivity)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.continueWatching.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.watchproviders.testing)
            }
        }
    }
}
