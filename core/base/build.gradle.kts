plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
    useSerialization()

    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.view)

            implementation(projects.core.logger.api)
            implementation(libs.coroutines.core)
            implementation(libs.decompose.decompose)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
