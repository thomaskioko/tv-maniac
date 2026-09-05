plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.connectivity.api)
                api(projects.core.logger.api)
                api(projects.data.accountManager.api)
                api(projects.data.subscription.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.connectivity.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.subscription.testing)
            }
        }
    }
}
