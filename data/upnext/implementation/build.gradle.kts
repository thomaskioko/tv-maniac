plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()

    optIn("kotlinx.coroutines.DelicateCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.data.datastore.api)
                api(projects.data.episode.api)
                api(projects.data.upnext.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
