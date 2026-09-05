plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.syncstate.api)
            api(libs.coroutines.core)
            implementation(projects.core.logger.api)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(projects.core.logger.testing)
        }
    }
}
