plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    optIn("kotlinx.coroutines.FlowPreview")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.data.followedshows.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
