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
                api(projects.core.util.api)
                api(projects.data.database.sqldelight)
                api(projects.data.followedshows.api)

                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)
            }
        }
    }
}
