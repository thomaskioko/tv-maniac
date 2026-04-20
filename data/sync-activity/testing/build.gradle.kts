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

                implementation(projects.core.base)
                implementation(projects.data.syncActivity.implementation)
                implementation(projects.data.database.sqldelight)
                implementation(libs.coroutines.core)
                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
