plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.database)
                api(projects.core.util)

                implementation(libs.ktor.serialization)
            }
        }
    }
}
