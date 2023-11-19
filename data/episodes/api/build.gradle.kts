plugins {
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.util)
                api(projects.core.database)

                api(libs.coroutines.core)
            }
        }
    }
}