plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.showdetails.api)
                implementation(projects.data.database.sqldelight)

                implementation(libs.coroutines.core)
            }
        }
    }
}
