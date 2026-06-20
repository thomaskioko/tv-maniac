plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(libs.firebase.config)
                implementation(libs.okio)
                implementation(libs.coroutines.play.services)
            }
        }

        commonMain.dependencies {
            api(projects.core.featureFlags.api)
            api(projects.core.appconfig.api)
            api(projects.core.base)
            api(projects.core.logger.api)

            api(libs.coroutines.core)
            api(libs.androidx.datastore.preference)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(projects.core.featureFlags.testing)
            implementation(projects.core.logger.testing)
        }
    }
}
