plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.seasondetails.api)
                api(projects.data.database.sqldelight)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinx.collections)
            }
        }
    }
}
