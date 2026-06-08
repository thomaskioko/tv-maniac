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
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.core.tasks.api)
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.episode.api)
                api(projects.data.library.api)

                implementation(projects.core.view)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.library.testing)
            }
        }
    }
}
