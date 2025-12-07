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
                api(projects.data.datastore.api)
                api(libs.coroutines.core)
                api(libs.kotlinx.datetime)
            }
        }
    }
}
