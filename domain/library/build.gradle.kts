plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.tasks.api)
                implementation(projects.core.util.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.library.api)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.syncActivity.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.data.watchproviders.api)

                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.library.testing)
            }
        }
    }
}
