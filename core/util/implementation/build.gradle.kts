plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget(withDeviceTestBuilder = true)
    useMetro()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlin.time.ExperimentalTime",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.coroutines.core)
            api(libs.kotlinx.datetime)
            api(projects.core.util.api)
            implementation(libs.kermit)
        }

        androidMain.dependencies {
            implementation(libs.kermit.core)
        }

        commonTest.dependencies {
            implementation(projects.core.util.testing)
            implementation(libs.bundles.unittest)
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }
    }
}
