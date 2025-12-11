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
                api(projects.data.database.sqldelight)
                api(projects.data.seasondetails.api)
                api(libs.coroutines.core)
                api(libs.kotlinx.collections)
                api(libs.kotlinx.datetime)
            }
        }
    }
}
