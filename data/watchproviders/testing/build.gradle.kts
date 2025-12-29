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
                api(projects.data.watchproviders.api)
                implementation(projects.data.database.sqldelight)

                implementation(libs.coroutines.core)
            }
        }
    }
}
