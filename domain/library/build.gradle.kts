plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(projects.data.episode.api)
                api(projects.data.syncActivity.api)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                api(projects.core.syncstate.api)
                api(projects.core.tasks.api)
                api(projects.core.util.api)
                api(projects.data.datastore.api)
                api(projects.data.followedshows.api)
                api(projects.data.library.api)
                api(projects.data.traktauth.api)
                api(projects.domain.showdetails)
                api(projects.domain.syncActivity)

                implementation(projects.data.requestManager.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
