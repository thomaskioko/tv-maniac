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
                api(projects.core.syncstate.api)
                api(projects.core.util.api)
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.rewatch.api)
                api(projects.data.shows.api)

                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.rewatch.testing)
                implementation(projects.data.shows.testing)
            }
        }
    }
}
