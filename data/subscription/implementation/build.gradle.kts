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
                api(projects.data.subscription.api)
                api(projects.core.featureFlags.api)
                api(projects.data.datastore.api)
                api(projects.core.appconfig.api)
                api(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.subscription.testing)
                implementation(projects.core.featureFlags.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.core.util.testing)
            }
        }
    }
}
