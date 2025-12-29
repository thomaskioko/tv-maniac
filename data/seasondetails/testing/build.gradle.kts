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
                api(projects.data.seasondetails.api)
                api(projects.data.database.sqldelight)

                implementation(libs.coroutines.core)
            }
        }
    }
}
