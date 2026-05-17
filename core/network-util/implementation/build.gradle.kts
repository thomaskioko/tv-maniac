plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()

    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.networkUtil.api)

            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.atomicfu)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
