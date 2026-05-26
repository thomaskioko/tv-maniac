plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                api(projects.core.syncstate.api)
                api(projects.data.requestManager.api)
                api(projects.data.seasondetails.api)
                api(projects.data.seasons.api)
                api(projects.data.startWatching.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.seasons.testing)
                implementation(projects.data.startWatching.testing)
            }
        }
    }
}
