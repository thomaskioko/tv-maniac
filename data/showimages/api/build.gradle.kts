plugins {
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.database)
                api(projects.core.util)

                api(libs.coroutines.core)
            }
        }
    }
}