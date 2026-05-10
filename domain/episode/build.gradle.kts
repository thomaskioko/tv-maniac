plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.shows.api)

                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.syncstate.api)
                implementation(projects.core.tasks.api)
                implementation(projects.core.util.api)
                implementation(projects.core.networkUtil.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.data.watchlist.api)

                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.watchlist.testing)
            }
        }
    }
}
