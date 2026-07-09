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
                api(projects.data.accountManager.api)
                api(projects.data.datastore.api)
                api(projects.data.followedshows.api)
                api(projects.data.library.api)
                api(projects.domain.showdetails)
                api(projects.domain.syncActivity)

                implementation(projects.data.requestManager.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.watchproviders.testing)
            }
        }
    }
}
