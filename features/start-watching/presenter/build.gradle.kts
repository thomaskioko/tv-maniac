plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.core.view)
                api(projects.data.watchlistPrefs.api)
                api(projects.domain.startWatching)
                api(projects.features.myShows.nav)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
                api(libs.coroutines.core)

                implementation(projects.data.startWatching.api)
                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.data.startWatching.testing)
                implementation(projects.data.watchlistPrefs.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
