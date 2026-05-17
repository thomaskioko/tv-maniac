plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.core.tasks.api)
                api(projects.core.util.api)
                api(projects.data.datastore.api)
                api(projects.data.followedshows.api)
                api(projects.data.library.api)
                api(projects.data.showdetails.api)
                api(projects.data.syncActivity.api)
                api(projects.data.traktauth.api)
                api(projects.data.watchproviders.api)

                implementation(projects.data.requestManager.api)
                implementation(projects.domain.episode)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
