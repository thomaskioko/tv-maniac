plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.database.sqldelight)
                implementation(projects.core.networkUtil.api)

                api(libs.coroutines.core)
            }
        }
    }
}
