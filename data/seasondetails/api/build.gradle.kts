plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.database.sqldelight)
                implementation(projects.core.networkUtil)

                api(libs.coroutines.core)
            }
        }
    }
}
