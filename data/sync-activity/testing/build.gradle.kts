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
                api(projects.core.networkUtil.api)
                api(projects.data.connectedAccount.api)
                api(projects.data.syncActivity.api)
                api(projects.data.database.sqldelight)
                api(projects.data.syncActivity.implementation)
                api(libs.coroutines.core)

                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
