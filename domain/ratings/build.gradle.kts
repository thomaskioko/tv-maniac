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
                api(projects.data.datastore.api)
                api(projects.data.ratings.api)
                api(projects.data.subscription.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.ratings.testing)
                implementation(projects.data.subscription.testing)
            }
        }
    }
}
