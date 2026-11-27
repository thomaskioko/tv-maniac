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
                api(projects.data.episode.api)
                api(projects.data.startWatching.api)
                api(projects.domain.showdetails)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.startWatching.testing)
                implementation(projects.data.watchproviders.testing)
            }
        }
    }
}
