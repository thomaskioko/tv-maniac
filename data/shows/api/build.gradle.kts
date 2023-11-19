plugins {
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.database)
                api(projects.data.category.api)

                api(libs.coroutines.core)
            }
        }
    }
}