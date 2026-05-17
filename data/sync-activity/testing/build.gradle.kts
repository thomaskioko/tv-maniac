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
                api(projects.data.syncActivity.api)
                api(projects.data.database.sqldelight)
                api(projects.data.syncActivity.implementation)
                api(libs.coroutines.core)

                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
