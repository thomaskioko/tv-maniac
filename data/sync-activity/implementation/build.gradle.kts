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
                api(libs.store5)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.util.api)
                api(projects.data.connectedAccount.api)
                api(projects.data.database.sqldelight)
                api(projects.data.requestManager.api)
                api(projects.data.syncActivity.api)
                api(projects.data.syncProvider.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.connectedAccount.testing)
                implementation(projects.data.database.testing)
            }
        }
    }
}
