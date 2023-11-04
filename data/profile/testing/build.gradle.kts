plugins {
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.database)
                implementation(projects.data.profile.api)
                implementation(libs.coroutines.core)
            }
        }
    }
}