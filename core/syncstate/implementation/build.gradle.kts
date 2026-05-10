plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.syncstate.api)
            implementation(libs.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
