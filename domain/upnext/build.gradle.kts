plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    useMetro()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.core.view)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.syncstate.api)
                api(projects.core.tasks.api)
                api(projects.core.util.api)
                api(projects.data.datastore.api)
                api(projects.data.traktauth.api)
                api(projects.data.upnext.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.upnext.testing)
            }
        }
    }
}
