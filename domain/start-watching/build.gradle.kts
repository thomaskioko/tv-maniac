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
                api(projects.data.startWatching.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.startWatching.testing)
            }
        }
    }
}
