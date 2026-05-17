plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
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
