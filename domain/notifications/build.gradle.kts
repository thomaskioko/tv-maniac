plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
    optIn("kotlinx.coroutines.FlowPreview")
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.i18n.generator)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                api(projects.core.notifications.api)
                api(projects.core.syncstate.api)
                api(projects.core.tasks.api)
                api(projects.core.util.api)
                api(projects.data.datastore.api)
                api(projects.data.episode.api)
                api(projects.data.seasondetails.api)
                api(projects.data.seasons.api)
                api(projects.data.traktauth.api)
                api(projects.i18n.api)

                implementation(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.notifications.testing)
                implementation(projects.core.util.testing)
                implementation(projects.core.view)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.i18n.generator)
                implementation(projects.i18n.testing)
            }
        }
    }
}
