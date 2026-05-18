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
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
