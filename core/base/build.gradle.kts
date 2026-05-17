plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()

    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(libs.essenty.lifecycle)
        }

        commonMain.dependencies {
            api(projects.core.view)
            api(projects.core.logger.api)
            api(libs.coroutines.core)
            api(libs.decompose.decompose)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
