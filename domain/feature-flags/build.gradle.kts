plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }

        commonMain.dependencies {
            api(libs.coroutines.core)
            api(libs.kotlinx.collections)
            api(projects.core.base)
            api(projects.core.featureFlags.api)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(projects.core.featureFlags.testing)
        }
    }
}
