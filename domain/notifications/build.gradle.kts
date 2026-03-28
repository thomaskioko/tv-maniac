plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useKotlinInject()
    optIn("kotlinx.coroutines.FlowPreview")
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.tasks.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.seasons.api)
                implementation(projects.core.logger.api)
                implementation(projects.core.networkUtil.api)
                implementation(projects.i18n.api)
                implementation(projects.core.notifications.api)
                implementation(projects.core.util.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.traktauth.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.notifications.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.i18n.testing)
            }
        }
    }
}
