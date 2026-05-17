plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

scaffold {
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.data.genre.api)

                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
