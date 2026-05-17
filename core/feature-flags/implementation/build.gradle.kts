plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.firebase.config)
                implementation(libs.coroutines.play.services)
            }
        }

        commonMain.dependencies {
            api(projects.core.featureFlags.api)
            implementation(projects.core.appconfig.api)
            implementation(projects.core.base)
            implementation(projects.core.logger.api)
            implementation(libs.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(projects.core.featureFlags.testing)
            implementation(projects.core.logger.testing)
        }
    }
}
